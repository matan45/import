package audio;

import javazoom.jl.decoder.*;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class MP3Decoder {

    private void test() {
        String mp3FilePath = "file_example_MP3_1MG.mp3";

        try {
            FileInputStream fileInputStream = new FileInputStream(Path.of(mp3FilePath).toAbsolutePath().toString());
            Bitstream bitstream = new Bitstream(fileInputStream);
            // Create a Decoder object.
            Decoder decoder = new Decoder();

            ByteArrayOutputStream pcmOutputStream = new ByteArrayOutputStream();
            double totalDurationInSeconds = 0;
            int sampleFrequency = 0;
            int channelCount = 0;
            while (true) {
                Header frameHeader = bitstream.readFrame();
                if (frameHeader == null) {
                    break;
                }

                totalDurationInSeconds += frameHeader.ms_per_frame() / 1000.0;

                // Decode the MP3 frame.
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frameHeader, bitstream);
                sampleFrequency = output.getSampleFrequency();
                channelCount = output.getChannelCount();

                short[] pcmData = output.getBuffer();
                // Convert and write the PCM data to pcmOutputStream
                for (short sample : pcmData) {
                    pcmOutputStream.write(sample & 0xFF);
                    pcmOutputStream.write((sample >> 8) & 0xFF);
                }
                bitstream.closeFrame();
            }

            bitstream.close();
            fileInputStream.close();

            byte[] pcmData = pcmOutputStream.toByteArray();

            System.out.println(sampleFrequency);
            System.out.println(channelCount);
            System.out.println(totalDurationInSeconds);
            // Now, pcmData contains the raw PCM audio data as a byte array
            // You can further process or save this data as needed
        } catch (JavaLayerException | IOException e) {
            e.printStackTrace();
        }

    }
}
