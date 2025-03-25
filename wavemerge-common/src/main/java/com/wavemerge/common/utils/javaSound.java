package com.wavemerge.common.utils;
import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

public class javaSound {
    public static void main(String[] args) throws UnsupportedAudioFileException, IOException {
        String basePath = "D:\\ruoyi\\uploadPath\\upload\\2024\\10\\21";
        File audioFile1 = new File(basePath, "01 _4_20241021155517A001.wav");
        File audioFile2 = new File(basePath, "03 _2_20241021162509A001.wav");

        AudioInputStream stream1 = AudioSystem.getAudioInputStream(audioFile1);
        AudioInputStream stream2 = AudioSystem.getAudioInputStream(audioFile2);

        AudioFormat format1 = stream1.getFormat();
        AudioFormat format2 = stream2.getFormat();

        if (!format1.matches(format2)) {
            throw new IllegalArgumentException("两个音频文件的格式不匹配");
        }

        // 创建输出格式和混合输出流
        AudioFormat outputFormat = format1;
        long frameLength = Math.min(stream1.getFrameLength(), stream2.getFrameLength());
        AudioInputStream mixedStream = new AudioInputStream(
                new AudioMixingStream(stream1, stream2, frameLength, outputFormat),
                outputFormat,
                frameLength
        );

        File outputFile = new File("mixed_output.wav");
        AudioSystem.write(mixedStream, AudioFileFormat.Type.WAVE, outputFile);
        System.out.println("音频混合完成并写入到文件：" + outputFile.getAbsolutePath());
    }
}

class AudioMixingStream extends AudioInputStream {
    private final AudioInputStream stream1;
    private final AudioInputStream stream2;

    public AudioMixingStream(AudioInputStream stream1, AudioInputStream stream2, long frameLength, AudioFormat format) {
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
            return -1;  // 读到流末尾
        }

        for (int i = 0; i < read1; i++) {
            // 简单混合两个音频信号（防止溢出）
            int mixedValue = buffer1[i] + buffer2[i];
            if (mixedValue > Byte.MAX_VALUE) {
                mixedValue = Byte.MAX_VALUE;
            } else if (mixedValue < Byte.MIN_VALUE) {
                mixedValue = Byte.MIN_VALUE;
            }
            buffer[i] = (byte) mixedValue;
        }

        return read1;
    }
}