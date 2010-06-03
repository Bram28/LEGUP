package edu.rpi.phil.legup.puzzles.treetent;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import edu.rpi.phil.legup.BoardState;

public class RuleNewTentLink extends RuleNewLink{

	
    public RuleNewTentLink()
    {
    	name = "New Link Tent";
    	description = "A tent must link to a tree if there are no unlinked trees near the tent.";
    	image = new ImageIcon("images/treetent/NewTentLink.png");
    }
    
    protected boolean checkCellNeededLink(Point tree, Point tent, BoardState state, ArrayList<Object> validLinks)
    {
    	if(checkTreeNeededLink(tree,tent,state,validLinks))
    		return true;
    	if(checkTentNeededLink(tree, tent, state,validLinks))
    		return true;
    	return false;
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
