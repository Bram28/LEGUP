package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;

public class RuleTreeForTent extends RuleNewLink
{
	static final long serialVersionUID = 9515L;
	public String getImageName() {return "images/treetent/NewTentLink.png";}
    public RuleTreeForTent()
    {
    	setName("Tree for Tent");
    	//description = "A tent must link to a tree if only one unlinked tree is near the tent.";
    	description = "If only one unlinked tree is adjacent to an unlinked tent, the unlinked tent must link to the unlinked tree.";
    	//image = new ImageIcon("images/treetent/NewTentLink.png");
    }

    protected String checkCellNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks)
    {
    	return this.checkTentNeededLink(tree,tent,state,validLinks);
    }
	protected boolean doDefaultApplicationRaw(BoardState destBoardState)
	{
		BoardState origBoardState = destBoardState.getSingleParentState();
		boolean changed = false;
		int width = destBoardState.getWidth();
		int height = destBoardState.getHeight();
		int num_connected;
		ArrayList <Object> destExtra = destBoardState.getExtraData();
		ArrayList <Object> origExtra = origBoardState.getExtraData();
		for(int x = 0; x<width;x++)
		{
			for(int y = 0; y<height;y++)
			{

				if(destBoardState.getCellContents(x,y)==TreeTent.CELL_TENT)
				{
					num_connected=0;
					if(TreeTent.isLinked(destExtra, new Point(x,y)))
						continue;
					for(int i =-1;i<2;i++)
					{
						for(int j = -1;j<2;j++)
						{
							if(i==0 && j==0)
								continue;
							if((x+i)>=width || (x+i)<0)
								continue;
							if((y+j)>=height || (y+j)<0)
								continue;
							if(i!=0 && j!=0)
								continue;

							if(destBoardState.getCellContents(x+i,y+j)==TreeTent.CELL_TREE)
							{
								System.out.println(x+" "+y+"         "+(x+i)+" "+(y+j));
								if(TreeTent.isLinked(origExtra, new Point(x+i,y+j)))
									continue;
								num_connected++;
							}
						}
					}
					if(num_connected==1)
					{
						for(int i =-1;i<2;i++)
						{
							for(int j = -1;j<2;j++)
							{
								if(i==0 && j==0)
									continue;
								if((x+i)>=width || (x+i)<0)
									continue;
								if((y+j)>=height || (y+j)<0)
									continue;
								if(i!=0 && j!=0)
									continue;

								if(destBoardState.getCellContents(x+i,y+j)==TreeTent.CELL_TREE)
								{
									if( TreeTent.isLinked(origExtra, new Point(x+i,y+j)) )
										continue;
									destExtra.add((Object) new ExtraTreeTentLink(new Point(x,y), new Point(x+i,y+j)));
									destBoardState.setExtraData(destExtra);
									changed=true;
								}
							}
						}
					}
				}
			}
		}
		String error = checkRuleRaw(destBoardState);
		if(error != null)
		{
			System.out.println(error);
			changed = false;
		}
		if(!changed)
		{
			destBoardState= origBoardState.copy();
		}
		return changed;
	}

}
