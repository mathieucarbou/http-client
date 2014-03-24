/**
 * Copyright (C) 2013 Mycila (mathieu@mycila.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mycila.hc.io

import java.nio.ByteBuffer
import java.util.zip.Inflater
import java.util.zip.ZipException

import static com.mycila.hc.io.GZIPContentDecoder.State.*

/**
 * @author Mathieu Carbou (mathieu.carbou@gmail.com)
 * @date 2014-02-27
 */
class GZIPContentDecoder implements ContentDecoder {

    public static final ByteBuffer EMPTY_BUFFER = ByteBuffer.wrap(new byte[0])

    protected final Inflater inflater = new Inflater(true)
    protected final byte[] bytes
    protected byte[] output
    protected State state
    protected int size
    protected int value
    protected byte flags

    public GZIPContentDecoder(int bufferSize) {
        this.bytes = new byte[bufferSize]
        reset()
    }

    @Override
    public ByteBuffer decode(ByteBuffer buffer) {
        while (buffer.hasRemaining()) {
            byte currByte = buffer.get()
            switch (state) {
                case INITIAL:
                    buffer.position(buffer.position() - 1)
                    state = ID
                    break
                case ID:
                    value += (currByte & 0xFF) << 8 * size
                    ++size
                    if (size == 2) {
                        if (value != 0x8B1F)
                            throw new ZipException("Invalid gzip bytes")
                        state = CM
                    }
                    break
                case CM:
                    if ((currByte & 0xFF) != 0x08)
                        throw new ZipException("Invalid gzip compression method")
                    state = FLG
                    break
                case FLG:
                    flags = currByte
                    state = MTIME
                    size = 0
                    value = 0
                    break
                case MTIME:
                    // Skip the 4 MTIME bytes
                    ++size
                    if (size == 4)
                        state = XFL
                    break
                case XFL:
                    // Skip XFL
                    state = OS
                    break
                case OS:
                    // Skip OS
                    state = FLAGS
                    break
                case FLAGS:
                    buffer.position(buffer.position() - 1)
                    if ((flags & 0x04) == 0x04) {
                        state = EXTRA_LENGTH
                        size = 0
                        value = 0
                    } else if ((flags & 0x08) == 0x08)
                        state = NAME
                    else if ((flags & 0x10) == 0x10)
                        state = COMMENT
                    else if ((flags & 0x2) == 0x2) {
                        state = HCRC
                        size = 0
                        value = 0
                    } else
                        state = DATA
                    break
                case EXTRA_LENGTH:
                    value += (currByte & 0xFF) << 8 * size
                    ++size
                    if (size == 2)
                        state = EXTRA
                    break
                case EXTRA:
                    // Skip EXTRA bytes
                    --value
                    if (value == 0) {
                        // Clear the EXTRA flag and loop on the flags
                        flags &= ~0x04
                        state = FLAGS
                    }
                    break
                case NAME:
                    // Skip NAME bytes
                    if (currByte == 0) {
                        // Clear the NAME flag and loop on the flags
                        flags &= ~0x08
                        state = FLAGS
                    }
                    break
                case COMMENT:
                    // Skip COMMENT bytes
                    if (currByte == 0) {
                        // Clear the COMMENT flag and loop on the flags
                        flags &= ~0x10
                        state = FLAGS
                    }
                    break
                case HCRC:
                    // Skip HCRC
                    ++size
                    if (size == 2) {
                        // Clear the HCRC flag and loop on the flags
                        flags &= ~0x02
                        state = FLAGS
                    }
                    break
                case DATA:
                    buffer.position(buffer.position() - 1)
                    while (true) {
                        int decoded = inflater.inflate(bytes)
                        if (decoded == 0) {
                            if (inflater.needsInput()) {
                                if (buffer.hasRemaining()) {
                                    byte[] input = new byte[buffer.remaining()]
                                    buffer.get(input)
                                    inflater.setInput(input)
                                } else {
                                    if (output != null) {
                                        ByteBuffer result = ByteBuffer.wrap(output)
                                        output = null
                                        return result
                                    }
                                    break
                                }
                            } else if (inflater.finished()) {
                                int remaining = inflater.getRemaining()
                                buffer.position(buffer.limit() - remaining)
                                state = CRC
                                size = 0
                                value = 0
                                break
                            } else {
                                throw new ZipException("Invalid inflater state")
                            }
                        } else {
                            if (output == null) {
                                // Save the inflated bytes and loop to see if we have finished
                                output = new byte[decoded]
                                System.arraycopy(bytes, 0, output, 0, decoded)
                            } else {
                                // Accumulate inflated bytes and loop to see if we have finished
                                byte[] newOutput = new byte[output.length + decoded]
                                System.arraycopy(output, 0, newOutput, 0, output.length)
                                System.arraycopy(bytes, 0, newOutput, output.length, decoded)
                                output = newOutput
                            }
                        }
                    }
                    break
                case CRC:
                    value += (currByte & 0xFF) << 8 * size
                    ++size
                    if (size == 4) {
                        // From RFC 1952, compliant decoders need not to verify the CRC
                        state = ISIZE
                        size = 0
                        value = 0
                    }
                    break
                case ISIZE:
                    value += (currByte & 0xFF) << 8 * size
                    ++size
                    if (size == 4) {
                        if (value != inflater.bytesWritten)
                            throw new ZipException("Invalid input size")
                        ByteBuffer result = output == null ? EMPTY_BUFFER : ByteBuffer.wrap(output)
                        reset()
                        return result
                    }
                    break
                default:
                    throw new ZipException()
            }
        }
        return EMPTY_BUFFER
    }

    protected void reset() {
        inflater.reset()
        Arrays.fill(bytes, (byte) 0)
        output = null
        state = INITIAL
        size = 0
        value = 0
        flags = 0
    }

    static enum State {
        INITIAL, ID, CM, FLG, MTIME, XFL, OS, FLAGS, EXTRA_LENGTH, EXTRA, NAME, COMMENT, HCRC, DATA, CRC, ISIZE
    }

}
