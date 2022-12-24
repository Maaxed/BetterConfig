package fr.max2.betterconfig.config.value;

import java.util.List;

import fr.max2.betterconfig.config.spec.IConfigTableSpec;

public interface IConfigTable extends IConfigNode
{
	List<IConfigNode> getEntryValues();
	
	@Override
	IConfigTableSpec getSpec();
	
	@Override
	default <P, R> R exploreNode(IConfigValueVisitor<P, R> visitor, P param)
	{
		return visitor.visitTable(this, param);
	}
}
