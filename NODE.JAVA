package projects.DFTC.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Random;

import org.jdom.Parent;

import java.util.Iterator;

import projects.surprise.nodes.messages.LcrMessage;
import projects.surprise.nodes.timers.MessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.edges.Edge;

public class Node extends sinalgo.nodes.Node {
	
	static final int MAX_UIN = 10000;
	private int myUin;
	Node parent;
	int HasParent=0;
	Node neighbor[] = new Node[6];
	Node used[] = new Node[6];
	int NbreNeighbor=0;
	int IsInitiator=0;
	int NbreUsed=0;
	
	@Override
	public void handleMessages(Inbox inbox) {
		LcrMessage l=(LcrMessage) inbox.next();
		System.out.println(this + " received from "+ l.getUin());
		if (NbreNeighbor==0) GetNeighbor();
		//int i=0;
		//for(i=0; i<NbreNeighbor; i++) System.out.println(this + " has " +neighbor[i].ID + " as my neighboor");
		
		treat(l);
		
	}
		
	
	private void GetNeighbor()	{
		Iterator<Edge> edgeIter = this.outgoingConnections.iterator();
		
		while(edgeIter.hasNext()){
			Edge e = edgeIter.next();
			if((e.startNode.ID == this.ID) ){
				//System.out.println(this + " has " +e.endNode.ID + "is my neighboor");
				//send(new LcrMessage(myUin), e.endNode);
				neighbor[NbreNeighbor]=(Node) e.endNode;
			}
			if( (e.endNode.ID == this.ID) ){
				//System.out.println(this + " has " + e.startNode.ID + "is my neighboor");
				neighbor[NbreNeighbor]=(Node) e.startNode;
			}
			NbreNeighbor++;
		}
	}
	private void treat(LcrMessage m) {
		int sender=m.getUin();
		NbreUsed++;
		if (HasParent==0)	{
			int i=0;
			HasParent=1;
			for(i=0 ; i<NbreNeighbor ; i++)
			{
				if (neighbor[i].ID!=sender) send(new LcrMessage(this.ID), neighbor[i]);
				else parent=neighbor[i];
			}
		}
		if (NbreUsed==NbreNeighbor)	{
			if (IsInitiator==1){
				System.out.println(this + " is taking decision ");
			}
			else	{
				send(new LcrMessage(this.ID), parent);
			}
		}
	}

	@Override
	public void init() {
	    Random rand = sinalgo.tools.Tools.getRandomNumberGenerator();
	    myUin = rand.nextInt(MAX_UIN); 
		System.out.println(this + " is initialized. UIN = " + myUin);
		if (this.ID==1)
		{
			IsInitiator=1;
			new MessageTimer(new LcrMessage(this.ID)).startRelative(1, this);
		}
	}

	@Override
	public void draw(Graphics g, PositionTransformation pt, boolean highlight) {
		drawNodeAsDiskWithText(g, pt, highlight, (new Integer(ID).toString()), 25, Color.white);
	}

	@NodePopupMethod(menuText="broadcast")
	public void broadcast() {
		broadcast(new LcrMessage(myUin));
	}
	
	@Override
	public void preStep() { }

	@Override
	public void neighborhoodChange() { }

	@Override
	public void postStep() {  }

	@Override
	public void checkRequirements() throws WrongConfigurationException { }
	
}
