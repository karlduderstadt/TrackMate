package fiji.plugin.trackmate.visualization.trackscheme;

import java.awt.Rectangle;

import com.mxgraph.canvas.mxGraphics2DCanvas;
import com.mxgraph.shape.mxLabelShape;
import com.mxgraph.view.mxCellState;

/**
 * This is a shape that is made to display a cell in a way that suits for our 
 * spots objects. It displays an image on the left, that scales with the cell dimension,
 * and a label on the right.
 *<p>
 * We re-used the JGraphX classes as far as we could, which turned to necessitate
 * only to recalculate the image bounds to have then scaling with the cell size.
 *   
 * @author Jean-Yves Tinevez <jeanyves.tinevez@gmail.com> Mar 15, 2011
 */
public class mxScaledLabelShape extends mxLabelShape {

	public static final String SHAPE_NAME = "scaledLabel";

	@Override
	public Rectangle getImageBounds(mxGraphics2DCanvas canvas, mxCellState state) {
		Rectangle cellR = state.getRectangle();
		int arc = getArcSize(cellR.width, cellR.height) / 2;
		int minSize = Math.min(cellR.width - arc*2, cellR.height - 4);
		Rectangle imageBounds = new Rectangle(cellR.x + arc, cellR.y+2, minSize, minSize);
		return imageBounds;
	}
	
}
