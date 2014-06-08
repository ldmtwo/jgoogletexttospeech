/**
 * ***********************************************************************
 * Compilation: javac FFT.java Execution: java FFT N Dependencies: Complex.java
 *
 * Compute the FFT and inverse FFT of a length N complex sequence. Bare bones
 * implementation that runs in O(N log N) time. Our goal is to optimize the
 * clarity of the code, rather than performance.
 *
 * Limitations ----------- - assumes N is a power of 2
 *
 * - not the most memory efficient algorithm (because it uses an object type for
 * representing complex numbers and because it re-allocates memory for the
 * subarray, instead of doing in-place or reusing a single temporary array)
 *
 ************************************************************************
 */
package org.ldtwo.GoTTS;
//cs.princeton.edu

import java.util.ArrayList;
import java.util.Arrays;
import static java.lang.Math.*;
import java.text.DecimalFormat;

public class FFT {

    public static Complex[] get(ArrayList<Integer> array, double sampleFreq) {
        System.out.println("SIZE=" + array.size());
        double newsize = Math.ceil(Math.log(array.size()) / Math.log(2));
        newsize = Math.pow(2, newsize);
        while (array.size() < newsize) {
            array.add(0);
        }
        Complex[] x = new Complex[array.size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = new Complex((double) (int) array.get(i), 0);
        }
        Complex[] y;
//        y = fft(x);
        y = cconvolve(x, x);
//        show(y, "FFT");
        return y;
    }

    public static double[] getFrequency(ArrayList<Integer> array, double sampleFreq) {
//        int i = 0, j;
        System.out.println("SIZE=" + array.size());
//        ArrayList<Double> out=new ArrayList();
//        while (i  < array.size()) {
//            final List list = array.subList(i , Math.min(i  + 1023, array.size() - 1));
//            i+=1024;
//            out.addAll(getFrequency(list,sampleFreq));
//        }
//        double[] ret=new double[out.size()];
//        i=0;
//        for(;i<ret.length;i++){
//            ret[i]=out.get(i);
//        }
//        return ret;
//    }
//
//    public static List<Double> getFrequency(List<Integer> array, double sampleFreq) {
        double newsize = Math.ceil(Math.log(array.size()) / Math.log(2));
        newsize = Math.pow(2, newsize);
        while (array.size() < newsize) {
            array.add(0);
        }
        /*
         N = 1024          // size of FFT and sample window
         Fs = 44100        // sample rate = 44.1 kHz
         data[N]           // input PCM data buffer
         fft[N * 2]        // FFT complex buffer (interleaved real/imag)
         magnitude[N / 2]  // power spectrum

         capture audio in data[] buffer
         apply window function to data[]

         // copy real input data to complex FFT buffer
         for i = 0 to N - 1
         fft[2*i] = data[i]
         fft[2*i+1] = 0

         perform in-place complex-to-complex FFT on fft[] buffer

         // calculate power spectrum (magnitude) values from fft[]
         for i = 0 to N / 2 - 1
         re = fft[2*i]
         im = fft[2*i+1]
         magnitude[i] = sqrt(re*re+im*im)

         // find largest peak in power spectrum
         max_magnitude = -INF
         max_index = -1
         for i = 0 to N / 2 - 1
         if magnitude[i] > max_magnitude
         max_magnitude = magnitude[i]
         max_index = i

         // convert index of largest peak to frequency
         freq = max_index * Fs / N
         */
        Complex[] x = new Complex[array.size()];
        for (int i = 0; i < x.length; i++) {
            x[i] = new Complex((double) (int) array.get(i), 0);
        }
        Complex[] y;
        y = fft(x);

        // take inverse FFT
//        Complex[] z = ifft(y);
//        show(z, "z = ifft(y)");
        // circular convolution of x with itself
//        Complex[] c = cconvolve(x, x);
//        show(c, "c = cconvolve(x, x)");
        // linear convolution of x with itself
//        Complex[] d = convolve(x, x);
//        y=d;
        double[] mag = new double[x.length + 1];
        double maxMag = Double.MIN_VALUE;
        int maxIdx = 0;
        for (int i = 0; i < x.length; i++) {
            mag[i] = y[i].abs() / sampleFreq;
            System.out.printf("%s, ", (int) (double) mag[i]);
            if (mag[i] > maxMag) {//get the max
                maxMag = mag[i];
                maxIdx = i;
            }
        }
        mag[x.length] = (double) maxIdx;
        System.out.printf("\n");

        return mag;
    }

    /**
     *
     * @param data large array
     * @param output 2*len of {real,imag} pairs for window
     * @param pos start offset of window
     * @param len Window size
     * @return
     */
    static public double[] fft(double[] data, double[] output, int pos, final int len) {

        if (log(len) / log(2) - ((int) log(len) / log(2)) == 0) {
            System.out.println(" - - - - - POWER OF 2 FFT ALGO - - - - - - ");
            Complex[] arr = new Complex[len];
            for (int i = 0; i < len; i++) {
                arr[i] = new Complex(data[i + pos], 0);
            }
            arr = fft(arr);
            double[] darr = new double[len * 2];
            for (int i = 0; i < len; i++) {
                darr[i * 2] = arr[i].re();
                darr[i * 2 + 1] = arr[i].im();
            }
            return darr;
        }
        double real, imag;
        int j, k, outIdx = 0;

        //double[] output=new double[len*2];
        for (j = 0; j < len; j++) /* loop for frequency index */ {
            real = imag = 0.0;			/* clear variables */

            for (k = 0; k < len; k++) /* loop for sums */ {
                real += data[pos + k] * cos((2 * PI * k * j) / len) ;//* sin(PI * j / (len - 1))
                imag += data[pos + k] * sin((2 * PI * k * j) / len)  ;//* flatTopWindowFunc(j, len);
            }
//            fprintf(output, "%d\t%f\t%f\n", j, real / i, imag / i);
            output[outIdx++] = hypot(real / len, imag / len) * sin(PI * j / (len - 1));
            //output[outIdx++] = real/len*sin(PI*j/(len-1));
//            output[outIdx++] = imag / len * sin(PI * j / (len - 1));
        }
        return output;
    }static public double[] fft_flatTopWindow(double[] data, double[] output, int pos, final int len) {

        if (log(len) / log(2) - ((int) log(len) / log(2)) == 0) {
            System.out.println(" - - - - - POWER OF 2 FFT ALGO - - - - - - ");
            Complex[] arr = new Complex[len];
            for (int i = 0; i < len; i++) {
                arr[i] = new Complex(data[i + pos], 0);
            }
            arr = fft(arr);
            double[] darr = new double[len * 2];
            for (int i = 0; i < len; i++) {
                darr[i * 2] = arr[i].re();
                darr[i * 2 + 1] = arr[i].im();
            }
            return darr;
        }
        double real, imag;
        int j, k, outIdx = 0;

        //double[] output=new double[len*2];
        for (j = 0; j < len; j++) /* loop for frequency index */ {
            real = imag = 0.0;			/* clear variables */

            for (k = 0; k < len; k++) /* loop for sums */ {
                real += data[pos + k] * cos((2 * PI * k * j) / len) ;//* sin(PI * j / (len - 1))
                imag += data[pos + k] * sin((2 * PI * k * j) / len)*flatTopWindowFunc(k, len)  ;//* flatTopWindowFunc(j, len);
            }
//            fprintf(output, "%d\t%f\t%f\n", j, real / i, imag / i);
            output[outIdx++] = hypot(real / len, imag / len) * flatTopWindowFunc(j, len);
            //output[outIdx++] = real/len*sin(PI*j/(len-1));
//            output[outIdx++] = imag / len * sin(PI * j / (len - 1));
        }
        return output;
    }

    private static double flatTopWindowFunc(int i, int len) {
        final double a0, a1, a2, a3, a4;
        a0 = 1;
        a1 = 1.93;
        a2 = 1.29;
        a3 = 0.388;
        a4 = 0.028;

        return a0 - a1 * cos(2 * PI * i / (len - 1)) + a2 * cos(4 * PI * i / (len - 1)) - a3 * cos(6 * PI * i / (len - 1)) + a4 * cos(8 * PI * i / (len - 1));
    }

    public short[] HanningWindow(short[] signal_in, int pos, int size) {
        for (int i = pos; i < pos + size; i++) {
            int j = i - pos; // j = index into Hann window function
            signal_in[i] = (short) (signal_in[i] * 0.5 * (1.0 - Math.cos(2.0 * Math.PI * j / size)));
        }
        return signal_in;
    }

    // compute the FFT of x[], assuming its length is a power of 2
    public static Complex[] fft(Complex[] x) {
        int N = x.length;

        // base case
        if (N == 1) {
            return new Complex[]{x[0]};
        }

        // radix 2 Cooley-Tukey FFT
        if (N % 2 != 0) {
            throw new RuntimeException("N is not a power of 2");
        }

        // fft of even terms
        Complex[] even = new Complex[N / 2];
        for (int k = 0; k < N / 2; k++) {
            even[k] = x[2 * k];
        }
        Complex[] q = fft(even);

        // fft of odd terms
        Complex[] odd = even;  // reuse the array
        for (int k = 0; k < N / 2; k++) {
            odd[k] = x[2 * k + 1];
        }
        Complex[] r = fft(odd);

        // combine
        Complex[] y = new Complex[N];
        for (int k = 0; k < N / 2; k++) {
            double kth = -2 * k * Math.PI / N;
            Complex wk = new Complex(Math.cos(kth), Math.sin(kth));
            y[k] = q[k].plus(wk.times(r[k]));
            y[k + N / 2] = q[k].minus(wk.times(r[k]));
        }
        return y;
    }

    // compute the inverse FFT of x[], assuming its length is a power of 2
    public static Complex[] ifft(Complex[] x) {
        int N = x.length;
        Complex[] y = new Complex[N];

        // take conjugate
        for (int i = 0; i < N; i++) {
            y[i] = x[i].conjugate();
        }

        // compute forward FFT
        y = fft(y);

        // take conjugate again
        for (int i = 0; i < N; i++) {
            y[i] = y[i].conjugate();
        }

        // divide by N
        for (int i = 0; i < N; i++) {
            y[i] = y[i].times(1.0 / N);
        }

        return y;

    }

    // compute the circular convolution of x and y
    public static Complex[] cconvolve(Complex[] x, Complex[] y) {

        // should probably pad x and y with 0s so that they have same length
        // and are powers of 2
        if (x.length != y.length) {
            throw new RuntimeException("Dimensions don't agree");
        }

        int N = x.length;

        // compute FFT of each sequence
        Complex[] a = fft(x);
        Complex[] b = fft(y);

        // point-wise multiply
        Complex[] c = new Complex[N];
        for (int i = 0; i < N; i++) {
            c[i] = a[i].times(b[i]);
        }

        // compute inverse FFT
        return ifft(c);
    }

    // compute the linear convolution of x and y
    public static Complex[] convolve(Complex[] x, Complex[] y) {
        Complex ZERO = new Complex(0, 0);

        Complex[] a = new Complex[2 * x.length];
        for (int i = 0; i < x.length; i++) {
            a[i] = x[i];
        }
        for (int i = x.length; i < 2 * x.length; i++) {
            a[i] = ZERO;
        }

        Complex[] b = new Complex[2 * y.length];
        for (int i = 0; i < y.length; i++) {
            b[i] = y[i];
        }
        for (int i = y.length; i < 2 * y.length; i++) {
            b[i] = ZERO;
        }

        return cconvolve(a, b);
    }

    // display an array of Complex numbers to standard output
    public static void show(Complex[] x, String title) {
        System.out.println(title);
        System.out.println("-------------------");
        for (int i = 0; i < x.length; i++) {
            System.out.println(x[i]);
        }
        System.out.println();
    }

    /**
     * *******************************************************************
     * Test client and sample execution
     *
     * % java FFT 4 x ------------------- -0.03480425839330703
     * 0.07910192950176387 0.7233322451735928 0.1659819820667019
     *
     * y = fft(x) ------------------- 0.9336118983487516 -0.7581365035668999 +
     * 0.08688005256493803i 0.44344407521182005 -0.7581365035668999 -
     * 0.08688005256493803i
     *
     * z = ifft(y) ------------------- -0.03480425839330703 0.07910192950176387
     * + 2.6599344570851287E-18i 0.7233322451735928 0.1659819820667019 -
     * 2.6599344570851287E-18i
     *
     * c = cconvolve(x, x) ------------------- 0.5506798633981853
     * 0.23461407150576394 - 4.033186818023279E-18i -0.016542951108772352
     * 0.10288019294318276 + 4.033186818023279E-18i
     *
     * d = convolve(x, x) ------------------- 0.001211336402308083 -
     * 3.122502256758253E-17i -0.005506167987577068 - 5.058885073636224E-17i
     * -0.044092969479563274 + 2.1934338938072244E-18i 0.10288019294318276 -
     * 3.6147323062478115E-17i 0.5494685269958772 + 3.122502256758253E-17i
     * 0.240120239493341 + 4.655566391833896E-17i 0.02755001837079092 -
     * 2.1934338938072244E-18i 4.01805098805014E-17i
     *
     ********************************************************************
     */
    public static void main(String[] args) {
        int N = 32;//Integer.parseInt(args[0]);
        DecimalFormat df = new DecimalFormat("0.000");
        int M = 16;
        for (int j = 0; j <= M; j++) {
            for (int i = 0; i <= M; i++) {
                System.out.printf("%-6s\t", df.format(sin(2 * PI * i * j / M)));
            }
            System.out.printf("\n");
        }

        Complex[] x = new Complex[N];
        int[] data = {0, 1, 2, 4, 8, 4, 2, 1, 0, -1, -2, -4, -8, -4, -2, -1, 0, 1, 2, 4, 8, 4, 2, 1, 0, -1, -2, -4, -8, -4, -2, -1};
        // original data
        for (int i = 0; i < N; i++) {
            System.out.printf("cos(PI*%s/%s)=%s\n", i, N, (int) (sin(PI * i / (N - 1)) * 10));
            x[i] = new Complex(data[i], 0);
//            x[i] = new Complex(-10*Math.random() + 1, 0);
        }
        show(x, "x");

        // FFT of original data
        Complex[] y = fft(x);
        show(y, "y = fft(x)");

        // take inverse FFT
        Complex[] z = ifft(y);
        show(z, "z = ifft(y)");

        // circular convolution of x with itself
        Complex[] c = cconvolve(x, x);
        show(c, "c = cconvolve(x, x)");

        // linear convolution of x with itself
        Complex[] d = convolve(x, x);
        show(d, "d = convolve(x, x)");
        AudioAnalyzer.plotFFT("", c, 500);
    }

}
