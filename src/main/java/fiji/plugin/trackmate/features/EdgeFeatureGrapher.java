/*-
 * #%L
 * Fiji distribution of ImageJ for the life sciences.
 * %%
 * Copyright (C) 2010 - 2021 Fiji developers.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package fiji.plugin.trackmate.features;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.SelectionModel;
import fiji.plugin.trackmate.gui.displaysettings.DisplaySettings;

public class EdgeFeatureGrapher extends AbstractFeatureGrapher
{

	private final List< DefaultWeightedEdge > edges;

	private final Model model;

	private final SelectionModel selectionModel;

	private final DisplaySettings ds;

	public EdgeFeatureGrapher(
			final List< DefaultWeightedEdge > edges,
			final String xFeature,
			final List< String > yFeatures,
			final Model model,
			final SelectionModel selectionModel,
			final DisplaySettings displaySettings )
	{
		super(
				xFeature,
				yFeatures,
				model.getFeatureModel().getEdgeFeatureDimensions().get( xFeature ),
				model.getFeatureModel().getEdgeFeatureDimensions(),
				model.getFeatureModel().getEdgeFeatureNames(),
				model.getSpaceUnits(),
				model.getTimeUnits() );
		this.edges = edges;
		this.model = model;
		this.selectionModel = selectionModel;
		this.ds = displaySettings;
	}

	@Override
	protected ModelDataset buildMainDataSet( final List< String > targetYFeatures )
	{
		return new EdgeCollectionDataset(
				model,
				selectionModel,
				ds,
				xFeature,
				targetYFeatures,
				edges );
	}
}
