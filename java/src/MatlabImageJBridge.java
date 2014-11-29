import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.WindowManager;
import ij.gui.Line;
import ij.gui.OvalRoi;
import ij.gui.PointRoi;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.ShortProcessor;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.util.StringTokenizer;


public class MatlabImageJBridge {
	public static ImageJ imagej;
	private static final String version = "1.0.0";
	private static boolean verbose = true;
	
	public MatlabImageJBridge() {
	}
	
	public static String version() {
		return version;
	}
	
	public static void start() {
		start(true);
	}

	public static void start(boolean v) {
		verbose = v;
		launch(null);
	}

	public static void start(String IJpath) {
		System.setProperty("plugins.dir", IJpath);
		verbose = true;
		setupExt(IJpath);
		launch(null);
	}

	public static void start(String homeDir, String IJpath, boolean v) {
		System.setProperty("plugins.dir", IJpath);
		System.setProperty("user.dir", homeDir);
		System.setProperty("user.home", homeDir);
		verbose = v;
		setupExt(IJpath);
		launch(null);
	}

	public static void setupExt(String IJpath) {
		if (System.getProperty("java.imagej") == null) {
			System.setProperty("java.ext.dirs", System.getProperty("java.ext.dirs") + ":" + IJpath + "/jre/lib/ext");
			System.setProperty("java.imagej", "set");
		}
	}

	public static void start(String args, String IJpath) {
		setupExt(IJpath);
		verbose = true;
		launch(args.split("\\s"));
	}

	public static void launch(String myargs[]) {
		if (verbose) {
			System.out.println("--------------------------------------------------------------");
			System.out.println("MIJ " + version + ": Matlab to ImageJ Interface");
			System.out.println("--------------------------------------------------------------");
			System.out.println("More Info: http://bigwww.epfl.ch/sage/soft/mij/");
			System.out.println("Help: MIJ.help");
			Runtime runtime = Runtime.getRuntime();
			System.out.println("JVM> " + version);
			System.out.println("JVM> Version: " + System.getProperty("java.version"));
			System.out.println("JVM> Total amount of memory: " + Math.round(runtime.totalMemory() / 1024) + " Kb");
			System.out.println("JVM> Amount of free memory: " + Math.round(runtime.freeMemory() / 1024) + " Kb");
		}

		if (imagej instanceof ImageJ) {
			if (verbose) {
				System.out.println("--------------------------------------------------------------");
				System.out.println("Status> ImageJ is already started.");
				System.out.println("--------------------------------------------------------------");
			}
			return;
		}

		// ///////////////////////////////
		// /////These are the important lines
		// //////////////////////////////////
		imagej = new ImageJ();
		System.out.println(imagej.getProgressBar().isShowing());
		
		if (myargs != null) {
			if (verbose) {
				System.out.println("ImageJ> Arguments:");
				for (int i = 0; i < myargs.length; i++)
					System.out.println(myargs[i]);
			}
			ImageJ.main(myargs);
		}
		// /////////////////////////////////

		if (imagej instanceof ImageJ) {
			if (verbose) {
				System.out.println("ImageJ> Version:" + IJ.getVersion());
				System.out.println("ImageJ> Memory:" + IJ.freeMemory());
				System.out.println("ImageJ> Directory plugins: " + (IJ.getDirectory("plugins") == null ? "Not specified" : IJ.getDirectory("plugins")));
				System.out.println("ImageJ> Directory macros: " + (IJ.getDirectory("macros") == null ? "Not specified" : IJ.getDirectory("macros")));
				System.out.println("ImageJ> Directory luts: " + (IJ.getDirectory("luts") == null ? "Not specified" : IJ.getDirectory("luts")));
				System.out.println("ImageJ> Directory image: " + (IJ.getDirectory("image") == null ? "Not specified" : IJ.getDirectory("image")));
				System.out.println("ImageJ> Directory imagej: " + (IJ.getDirectory("imagej") == null ? "Not specified" : IJ.getDirectory("imagej")));
				System.out.println("ImageJ> Directory startup: " + (IJ.getDirectory("startup") == null ? "Not specified" : IJ.getDirectory("startup")));
				System.out.println("ImageJ> Directory home: " + (IJ.getDirectory("home") == null ? "Not specified" : IJ.getDirectory("home")));
				System.out.println("--------------------------------------------------------------");
				System.out.println("Status> ImageJ is running.");
				System.out.println("--------------------------------------------------------------");
			}
			
			imagej.getComponents()[0].repaint();
			
			imagej.addWindowStateListener(new WindowStateListener () {
				
				public void windowStateChanged(WindowEvent state) {
					
					/*if(state.getNewState() == 1 || state.getNewState() == 7) {
						System.out.println("窗口最小化");
					}else if(state.getNewState() == 0) {
						System.out.println("窗口恢复到初始状态");
					}else if(state.getNewState() == 6) {
						System.out.println("窗口最大化");
					}*/
					
					imagej.getComponents()[0].repaint();
				}
			});
			return;
		}
		if (verbose) {
			System.out.println("--------------------------------------------------------------");
			System.out.println("Status> ImageJ can not be started.");
			System.out.println("--------------------------------------------------------------");
		}
		IJ.getInstance().setTitle("ImageJ [MIJ " + version + "]");
	}
	
	public static void quit() {
		IJ.getInstance().quit();
		imagej = null;
		if (verbose && !(imagej instanceof ImageJ)) {
			System.out.println("ImageJ instance ended cleanly");
		}
	}
	
	public static Object getPixels(ImagePlus imageplus) {
		if (imageplus == null)
			return null;
		int width = imageplus.getWidth();
		int height = imageplus.getHeight();
		int stackSize = imageplus.getStackSize();
		int counter = 0;
		ImageStack imagestack = imageplus.getStack();
		switch (imageplus.getType()) {

		case ImagePlus.COLOR_256: {
			;
		}
		case ImagePlus.GRAY8: {
			byte[][][] is = new byte[height][width][stackSize];
			for (int sz = 0; sz < stackSize; sz++) {
				ByteProcessor byteprocessor = (ByteProcessor) imagestack.getProcessor(sz + 1);
				byte[] pixels = (byte[]) byteprocessor.getPixels();
				counter = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][sz] = pixels[counter];
						w++;
						counter++;
					}
					counter = ++h * width;
				}
			}
			return is;
		}
		case ImagePlus.GRAY16: {
			short[][][] is = new short[height][width][stackSize];
			for (int sz = 0; sz < stackSize; sz++) {
				counter = 0;
				ShortProcessor shortprocessor = (ShortProcessor) imagestack.getProcessor(sz + 1);
				short[] spixels = (short[]) shortprocessor.getPixels();
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][sz] = spixels[counter];
						w++;
						counter++;
					}
					counter = ++h * width;
				}
			}
			return is;
		}
		case ImagePlus.GRAY32: {
			double[][][] fs = new double[height][width][stackSize];
			for (int sz = 0; sz < stackSize; sz++) {
				FloatProcessor floatprocessor = (FloatProcessor) imagestack.getProcessor(sz + 1);
				float[] fpixels = (float[]) floatprocessor.getPixels();
				counter = 0;
				int i = 0;
				while (i < height) {
					int j = 0;
					while (j < width) {
						fs[i][j][sz] = (double) fpixels[counter];
						j++;
						counter++;
					}
					counter = ++i * width;
				}
			}
			return fs;
		}
		case ImagePlus.COLOR_RGB: {
			if (stackSize == 1) {
				byte[][][] is = new byte[height][width][3];
				ColorProcessor colorprocessor = (ColorProcessor) imagestack.getProcessor(1);
				byte[] red = new byte[width * height];
				byte[] green = new byte[width * height];
				byte[] blue = new byte[width * height];
				colorprocessor.getRGB(red, green, blue);
				counter = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][0] = red[counter];
						is[h][w][1] = green[counter];
						is[h][w][2] = blue[counter];
						w++;
						counter++;
					}
					counter = ++h * width;
				}
				return is;
			}
			byte[][][][] is = new byte[height][width][stackSize][3];
			for (int sz = 0; sz < stackSize; sz++) {
				ColorProcessor colorprocessor = (ColorProcessor) imagestack.getProcessor(sz + 1);
				byte[] red = new byte[width * height];
				byte[] green = new byte[width * height];
				byte[] blue = new byte[width * height];
				colorprocessor.getRGB(red, green, blue);
				counter = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						is[h][w][sz][0] = red[counter];
						is[h][w][sz][1] = green[counter];
						is[h][w][sz][2] = blue[counter];
						w++;
						counter++;
					}
					counter = ++h * width;
				}
			}
			return is;
		}
		default:
			System.out.println("MIJ Error message: Unknow type of volumes.");
			return null;
		}
	}
	
	public static ImagePlus createImage(Object object) {
		return createImage("Import from Matlab", object, true);
	}

	public static ImagePlus createImage(String title, Object object, boolean showImage) {
		ImagePlus imp = null;
		int i = 0;
		if (object instanceof byte[][]) {
			byte[][] is = (byte[][]) object;
			int height = is.length;
			int width = is[0].length;
			ByteProcessor byteprocessor = new ByteProcessor(width, height);
			byte[] bp = (byte[]) byteprocessor.getPixels();
			int h = 0;
			while (h < height) {
				int w = 0;
				while (w < width) {
					bp[i] = is[h][w];
					w++;
					i++;
				}
				i = ++h * width;
			}
			imp = new ImagePlus(title, byteprocessor);

		} else if (object instanceof short[][]) {
			short[][] is = (short[][]) object;
			int height = is.length;
			int width = is[0].length;
			ShortProcessor shortprocessor = new ShortProcessor(width, height);
			short[] sp = (short[]) shortprocessor.getPixels();
			int h = 0;
			while (h < height) {
				int w = 0;
				while (w < width) {
					sp[i] = is[h][w];
					w++;
					i++;
				}
				i = ++h * width;
			}
			imp = new ImagePlus(title, shortprocessor);

		} else if (object instanceof int[][]) {
			if (verbose)
				System.out.println("MIJ warning message: Loss of precision: convert int 32-bit to short 16-bit");
			int[][] is = (int[][]) object;
			int height = is.length;
			int width = is[0].length;
			ShortProcessor shortprocessor = new ShortProcessor(width, height);
			short[] sp = (short[]) shortprocessor.getPixels();
			int h = 0;
			while (h < height) {
				int w = 0;
				while (w < width) {
					sp[i] = (short) is[h][w];
					w++;
					i++;
				}
				i = ++h * width;
			}
			imp = new ImagePlus(title, shortprocessor);
		} else if (object instanceof float[][]) {
			float[][] fs = (float[][]) object;
			int height = fs.length;
			int width = fs[0].length;
			FloatProcessor floatprocessor = new FloatProcessor(width, height);
			float[] fp = (float[]) floatprocessor.getPixels();
			int h = 0;
			while (h < height) {
				int w = 0;
				while (w < width) {
					fp[i] = fs[h][w];
					w++;
					i++;
				}
				i = ++h * width;
			}
			floatprocessor.resetMinAndMax();
			imp = new ImagePlus(title, floatprocessor);

		} else if (object instanceof double[][]) {
			if (verbose)
				System.out.println("MIJ warning message: Loss of precision: convert double 32-bit to float 32-bit");
			double[][] ds = (double[][]) object;
			int height = ds.length;
			int width = ds[0].length;
			FloatProcessor floatprocessor = new FloatProcessor(width, height);
			float[] fp = (float[]) floatprocessor.getPixels();
			int h = 0;
			while (h < height) {
				int w = 0;
				while (w < width) {
					fp[i] = (float) ds[h][w];
					w++;
					i++;
				}
				i = ++h * width;
			}
			floatprocessor.resetMinAndMax();
			imp = new ImagePlus(title, floatprocessor);

		} else if (object instanceof byte[][][]) {
			byte[][][] is = (byte[][][]) object;
			int height = is.length;
			int width = is[0].length;
			int stackSize = is[0][0].length;
			ImageStack imagestack = new ImageStack(width, height);
			for (int sz = 0; sz < stackSize; sz++) {
				ByteProcessor byteprocessor = new ByteProcessor(width, height);
				byte[] bp = (byte[]) byteprocessor.getPixels();
				i = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						bp[i] = is[h][w][sz];
						w++;
						i++;
					}
					i = ++h * width;
				}
				imagestack.addSlice("", byteprocessor);
			}
			imp = new ImagePlus(title, imagestack);

		} else if (object instanceof short[][][]) {
			short[][][] is = (short[][][]) object;
			int height = is.length;
			int width = is[0].length;
			int stackSize = is[0][0].length;
			ImageStack imagestack = new ImageStack(width, height);
			for (int sz = 0; sz < stackSize; sz++) {
				ShortProcessor shortprocessor = new ShortProcessor(width, height);
				short[] sp = (short[]) shortprocessor.getPixels();
				i = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						sp[i] = is[h][w][sz];
						w++;
						i++;
					}
					i = ++h * width;
				}
				imagestack.addSlice("", shortprocessor);
			}
			imp = new ImagePlus(title, imagestack);

		} else if (object instanceof int[][][]) {
			if (verbose)
				System.out.println("MIJ warning message: Loss of precision: convert int 32 bits to short 16 bits");
			int[][][] is = (int[][][]) object;
			int height = is.length;
			int width = is[0].length;
			int stackSize = is[0][0].length;
			ImageStack imagestack = new ImageStack(width, height);
			for (int sz = 0; sz < stackSize; sz++) {
				ShortProcessor shortprocessor = new ShortProcessor(width, height);
				short[] sp = (short[]) shortprocessor.getPixels();
				i = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						sp[i] = (short) is[h][w][sz];
						w++;
						i++;
					}
					i = ++h * width;
				}
				if (sz == 0)
					shortprocessor.resetMinAndMax();
				imagestack.addSlice("", shortprocessor);

			}
			imp = new ImagePlus(title, imagestack);

		} else if (object instanceof float[][][]) {
			float[][][] fs = (float[][][]) object;
			int height = fs.length;
			int width = fs[0].length;
			int stackSize = fs[0][0].length;
			ImageStack imagestack = new ImageStack(width, height);
			for (int sz = 0; sz < stackSize; sz++) {
				FloatProcessor floatprocessor = new FloatProcessor(width, height);
				float[] fp = (float[]) floatprocessor.getPixels();
				i = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						fp[i] = fs[h][w][sz];
						w++;
						i++;
					}
					i = ++h * width;
				}
				if (sz == 0)
					floatprocessor.resetMinAndMax();
				imagestack.addSlice("", floatprocessor);
			}
			imp = new ImagePlus(title, imagestack);

		} else if (object instanceof double[][][]) {
			if (verbose)
				System.out.println("MIJ warning message: Loss of precision: convert double 32-bit to float 32-bit");
			double[][][] ds = (double[][][]) object;
			int height = ds.length;
			int width = ds[0].length;
			int stackSize = ds[0][0].length;
			ImageStack imagestack = new ImageStack(width, height);
			for (int sz = 0; sz < stackSize; sz++) {
				FloatProcessor floatprocessor = new FloatProcessor(width, height);
				float[] fp = (float[]) floatprocessor.getPixels();
				i = 0;
				int h = 0;
				while (h < height) {
					int w = 0;
					while (w < width) {
						fp[i] = (float) ds[h][w][sz];
						w++;
						i++;
					}
					i = ++h * width;
				}
				if (sz == 0)
					floatprocessor.resetMinAndMax();
				imagestack.addSlice("", floatprocessor);
			}
			imp = new ImagePlus(title, imagestack);

		} else {
			System.out.println("MIJ Error message: Unknow type of images or volumes.");
			return null;
		}

		if (showImage) {
			showImpWithListener(imp);
			imp.getChannelProcessor().resetMinAndMax();
			imp.updateAndDraw();
		}
		return imp;
	}
	
	public static ImagePlus showImpWithListener(final ImagePlus imp){
		imp.show();
		imp.getWindow().repaint();
		
		imp.getWindow().addComponentListener(new ComponentAdapter() {//添加组件监听器
            public void componentResized(ComponentEvent e) {//组件事件：调整大小
            	imp.getWindow().repaint();
            }
        });
		return imp;
	}

}
