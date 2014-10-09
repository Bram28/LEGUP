/*
*   Skyscrapers.java
*   Created by Anthony Handwerker
*   on October 9, 2014
*
*   Most recently updated
*   by Anthony Handwerker
*   on October 9, 2014
*/

package edu.rpi.phil.legup.puzzles.sudoku;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Vector;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JDialog;

import edu.rpi.phil.legup.AI;
import edu.rpi.phil.legup.BoardImage;
import edu.rpi.phil.legup.BoardState;
import edu.rpi.phil.legup.CaseRule;
import edu.rpi.phil.legup.Contradiction;
import edu.rpi.phil.legup.PuzzleGeneration;
import edu.rpi.phil.legup.PuzzleModule;
import edu.rpi.phil.legup.PuzzleRule;

public class Skyscrapers extends PuzzleModule
{  
  Vector <PuzzleRule> ruleList;
  Vector <Contradiction> contraList;
  Vector <CaseRule> caseList;

  public Skyscrapers()
  {
    ruleList = new Vector <PuzzleRule>();
    contraList = new Vector <Contradiction>();
    caseList = new Vector <CaseRule>();
  }
}
