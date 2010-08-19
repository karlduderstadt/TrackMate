//package fiji.plugin.nperry.features;
//
//import mpicbg.imglib.container.array.ArrayContainerFactory;
//import mpicbg.imglib.cursor.special.SphereCursor;
//import mpicbg.imglib.image.Image;
//import mpicbg.imglib.image.ImageFactory;
//import mpicbg.imglib.type.numeric.RealType;
//import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
//import fiji.plugin.nperry.Feature;
//import fiji.plugin.nperry.Spot;
//
//public class EstimatedRadius <T extends RealType<T>> extends IndependentFeatureAnalyzer {
//
//	private static final double MIN_DIAMETER_RATIO = 0.4;
//	private static final double MAX_DIAMETER_RATIO = 2;
//	
//	/** The number of different diameters to try. */
//	private int nDiameters;
//	private Image<T> img;
//	private double diam;
//	private double[] calibration;
//
//	/**
//	 * Create a feature analyzer that will return the best estimated diameter for a 
//	 * spot. Estimated diameter is obtained by finding the diameter that gives the 
//	 * maximum contrast, as calculated by the {@link BlobContrast} feature analyzer.
//	 * Searched diameters are linearly spread between <code>diameter</code> * {@value #MIN_DIAMTER_RATIO}
//	 * and <code>diameter</code> * {@value #MAX_DIAMETER_RATIO}. The optimum is them calculated by doing an interpolation
//	 * over calculated values.
//	 *  
//	 * @param originalImage  the image to get data from 
//	 * @param diameter  the diameter scale to search around
//	 * @param nDiameters  the number of different diameter to compute
//	 * @param calibration  the spatial calibration array containing the pixel size in X, Y, Z
//	 */
//	public EstimatedRadius(Image<T> originalImage, double diameter, int nDiameters,  double[] calibration) {
//		this.img = originalImage;
//		this.diam = diameter;
//		this.nDiameters = nDiameters;
//		this.calibration = calibration;
//	}
//
//	private static final Feature FEATURE = Feature.ESTIMATED_DIAMETER;
//	
//	@Override
//	public Feature getFeature() {
//		return FEATURE;
//	}
//
//	@Override
//	public boolean isNormalized() {
//		return false;
//	}
//
//	@Override
//	public void process(Spot spot) {
//		
//		// Get diameter array and radius squared
//		final double[] diameters = prepareDiameters(diam, nDiameters);
//		final double[] r2 = new double[nDiameters];
//		for (int i = 0; i < r2.length; i++) 
//			r2[i] = diameters[i] * diameters[i] / 4 ;
//		
//		// Calculate total intensity in balls
//		final double[] ring_intensities = new double[nDiameters];
//		final int[]    ring_volumes = new int[nDiameters];
//
//		final SphereCursor<T> cursor = new SphereCursor<T>(img, spot.getCoordinates(), diameters[nDiameters-2]/2, calibration); // sphere over the largest radius
//		double d2;
//		int i;
//		while(cursor.hasNext())  {
//			cursor.fwd();
//			d2 = cursor.getDistanceSquared();
//			for(i = 0 ; i < nDiameters-1 && d2 > r2[i] ; i++) {}
//			ring_intensities[i] += cursor.getType().getRealDouble();
//			ring_volumes[i]++;
//		}
//
//		// Calculate mean intensities from ring volumes
//		final double[] mean_intensities = new double[diameters.length];
//		for (int j = 0; j < mean_intensities.length; j++) 
//			mean_intensities[j] = ring_intensities[j] / ring_volumes[j];
//		
//		// Calculate contrasts as minus difference between outer and inner rings mean intensity
//		final double[] contrasts = new double[diameters.length - 1];
//		for (int j = 0; j < contrasts.length; j++) {
//			contrasts[j] = - ( mean_intensities[j+1] - mean_intensities[j] );
////			System.out.println(String.format("For diameter %.1f, found constrat of %.1f", diameters[j], contrasts[j])); 
//		}
//		
//		// Find max contrast
//		double maxConstrast = Double.NEGATIVE_INFINITY;
//		int maxIndex = 0;
//		for (int j = 0; j < contrasts.length; j++) {
//			if (contrasts[j] > maxConstrast) {
//				maxConstrast = contrasts[j];
//				maxIndex = j;
//			}
//		}
//		
//		double bestDiameter;
//		if ( 1 >= maxIndex || contrasts.length-1 == maxIndex) {
//			bestDiameter = diameters[maxIndex];
//		} else {
//			bestDiameter = quadratic1DInterpolation(
//					diameters[maxIndex-1], contrasts[maxIndex-1],
//					diameters[maxIndex], contrasts[maxIndex],
//					diameters[maxIndex+1], contrasts[maxIndex+1]);
//		}
//		spot.addFeature(FEATURE, bestDiameter);		
//	}
//	
//	private static final double quadratic1DInterpolation(double x1, double y1, double x2, double y2, double x3, double y3) {
//		final double d2 = 2 * ( (y3-y2)/(x3-x2) - (y2-y1)/(x2-x1) ) / (x3-x1);
//		if (d2==0)
//			return x2;
//		else {
//			final double d1 = (y3-y2)/(x3-x2) - d2/2 * (x3-x2);
//			return x2 -d1/d2;
//		}
//	}
//	
//	private static final double[] prepareDiameters(double centralDiameter, int nDiameters) {
//		final double[] diameters = new double[nDiameters];
//		for (int i = 0; i < diameters.length; i++) {
//			diameters[i] = centralDiameter * ( MIN_DIAMETER_RATIO   
//				+ i * (MAX_DIAMETER_RATIO - MIN_DIAMETER_RATIO)/(nDiameters-1) );
//		}
//		return diameters;
//	}
//	
//	
//	/**
//	 * For testing purposes
//	 */
//	public static void main(String[] args) {
//		
//		final byte on = (byte) 255;
//		Spot s1 = new Spot(new double[] {100, 100, 100});
//		Spot s2 = new Spot(new double[] {100, 100, 200});
//		Spot s3 = new Spot(new double[] {100, 100, 300});
//		Spot[] spots = new Spot[] {s1, s2, s3};
//		double[] radiuses = new double[]  {12, 20, 32};
//		double[] calibration = null; //new double[] {1, 1, 1};
//		
//		// Create 3 spots image
//		Image<UnsignedByteType> testImage = new ImageFactory<UnsignedByteType>(
//					new UnsignedByteType(),
//					new ArrayContainerFactory()
//				).createImage(new int[] {200, 200, 400});
//
//		SphereCursor<UnsignedByteType> cursor;
//		int index = 0;
//		for (Spot s : spots) {
//			cursor = new SphereCursor<UnsignedByteType>(
//					testImage,
//					s.getCoordinates(),
//					radiuses[index],
//					calibration);
//			while (cursor.hasNext())
//				cursor.next().set(on);
//			cursor.close();
//			index++;			
//		}
//				
//		ij.ImageJ.main(args);
//		ij.ImagePlus imp = mpicbg.imglib.image.display.imagej.ImageJFunctions.copyToImagePlus(testImage);
//		imp.show();
//		
//		// Apply the estimator
//		EstimatedRadius<UnsignedByteType> es = new EstimatedRadius<UnsignedByteType>(
//				testImage, 
//				40.5, 
//				20, 
//				calibration);
//		
//		Spot s;
//		double r;
//		long start, stop;
//		for (int i = 0; i < spots.length; i++) {
//			s = spots[i];
//			r = radiuses[i];
//			start = System.currentTimeMillis();
//			es.process(s);
//			stop = System.currentTimeMillis();
//			System.out.println(String.format("For spot %d, found diameter %.1f, real value was %.1f.", i, s.getFeatures().get(FEATURE), 2*r));
//			System.out.println("Computing time: "+(stop-start)+" ms.");
//		}
//	}
//}