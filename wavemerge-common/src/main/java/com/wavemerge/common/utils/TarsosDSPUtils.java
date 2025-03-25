package com.wavemerge.common.utils;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.jvm.AudioDispatcherFactory;
import be.tarsos.dsp.io.jvm.WaveformWriter;
import be.tarsos.dsp.writer.WriterProcessor;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class TarsosDSPUtils {


    public static byte[] floatToByteArray(float[] floatData, AudioFormat format) {
        int bytesPerSample = format.getSampleSizeInBits() / 8;
        byte[] byteData = new byte[floatData.length * bytesPerSample];
        boolean isBigEndian = format.isBigEndian();
        boolean isSigned = format.getEncoding() == AudioFormat.Encoding.PCM_SIGNED;

        for (int i = 0; i < floatData.length; i++) {
            int sample = (int) (floatData[i] * Math.pow(2, format.getSampleSizeInBits() - 1));

            for (int byteIndex = 0; byteIndex < bytesPerSample; byteIndex++) {
                int value = (sample >> (8 * byteIndex)) & 0xFF;

                if (isBigEndian) {
                    byteData[i * bytesPerSample + (bytesPerSample - byteIndex - 1)] = (byte) value;
                } else {
                    byteData[i * bytesPerSample + byteIndex] = (byte) value;
                }
            }
        }
        return byteData;
    }


    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        String basePath = "E:\\zwc\\test";
        String filePath_1 = Paths.get(basePath, "1.wav").toString();
        String filePath_2 = Paths.get(basePath, "2.wav").toString();

        // 读取两个音频文件
        AudioInputStream audioInputStream1 = AudioSystem.getAudioInputStream(new File(filePath_1));
        AudioInputStream audioInputStream2 = AudioSystem.getAudioInputStream(new File(filePath_2));

        // 获取两个音频输入流的格式，确保它们相同
        AudioFormat format1 = audioInputStream1.getFormat();
        AudioFormat format2 = audioInputStream2.getFormat();

        if (!format1.matches(format2)) {
            throw new IllegalArgumentException("两个音频文件格式不匹配，请确保采样率、位深度和声道数一致");
        }

        // 获取音频的帧长度
        long frameLength = Math.min(audioInputStream1.getFrameLength(), audioInputStream2.getFrameLength());

        // 创建输出文件
        String outputFilePath = "mixed_output.wav";
        AudioFormat outputFormat = format1;
        File outputFile = new File(outputFilePath);
        AudioInputStream outputAudioInputStream = new AudioInputStream(
                new MixingAudioStream(audioInputStream1, audioInputStream2, frameLength, outputFormat),
                outputFormat,
                frameLength
        );

        // 将混合的音频写入输出文件
        AudioSystem.write(outputAudioInputStream, AudioFileFormat.Type.WAVE, outputFile);

        System.out.println("音频混合完成并已写入到文件： " + outputFilePath);
    }

    static class MixingAudioStream extends AudioInputStream {

        private final AudioInputStream stream1;
        private final AudioInputStream stream2;

        public MixingAudioStream(AudioInputStream stream1, AudioInputStream stream2, long frameLength, AudioFormat format) {
            super(new ByteArrayInputStream(new byte[0]), format, frameLength);
            this.stream1 = stream1;
            this.stream2 = stream2;
        }

        @Override
        public int read(byte[] buffer, int offset, int length) throws IOException {
            byte[] buffer1 = new byte[length];
            byte[] buffer2 = new byte[length];
            int read1 = stream1.read(buffer1);
            int read2 = stream2.read(buffer2);

            if (read1 == -1 || read2 == -1) {
                return -1;
            }

            float[] floatBuffer1 = new float[read1 / getFormat().getFrameSize()];
            float[] floatBuffer2 = new float[read2 / getFormat().getFrameSize()];


            float[] mixedBuffer = new float[floatBuffer1.length];
            for (int i = 0; i < floatBuffer1.length; i++) {
                mixedBuffer[i] = (floatBuffer1[i] + floatBuffer2[i]) / 2;  // 混合并缩放以防止失真
            }

            //converter.toByteArray(mixedBuffer, buffer);
            return read1;
        }
    }
}

