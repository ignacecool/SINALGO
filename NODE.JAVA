package projects.DFTC.nodes.nodeImplementations;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.Random;

import projects.DFTC.nodes.messages.LcrMessage;
import projects.DFTC.nodes.timers.MessageTimer;
import sinalgo.configuration.WrongConfigurationException;
import sinalgo.gui.transformation.PositionTransformation;
import sinalgo.nodes.messages.Inbox;
import sinalgo.nodes.messages.Message;
import sinalgo.nodes.edges.Edge;

public class Node extends sinalgo.nodes.Node {
	
	static final int MAX_UIN = 10000;
	private int myUin;
	
	@Override
	public void handleMessages(Inbox inbox) {
		while(inbox.hasNext()) {
			Message msg = inbox.next();
			if (msg instanceof LcrMessage) {
				treat((LcrMessage) msg);
			} 
		}
	}
		
	private void treat(LcrMessage m) {
		int uin = m.getUin();
		System.out.println(this + " receives uin " + uin);
		if (uin == myUin) {
			this.setColor(Color.BLUE);
			System.out.println(this + " is YOOOYOY the leader");
			Iterator<Edge> edgeIter = this.outgoingConnections.iterator();
			while(edgeIter.hasNext()){
				Edge e = edgeIter.next();
				if(e.startNode.ID == this.ID){
					System.out.println(e.endNode.ID + "is my neighboor");
				}
				if(e.endNode.ID == this.ID){
					System.out.println(e.startNode.ID + "is my neighboor");
				}
			}
			//this.outgoingConnections.iterator();
		} else if (uin > myUin) {
			System.out.println(this + " forwards " + uin);
			//broadcast(new LcrMessage(uin));
			Iterator<Edge> edgeIter = this.outgoingConnections.iterator();
			while(edgeIter.hasNext()){
				Edge e = edgeIter.next();
				if(e.startNode.ID == this.ID){
					System.out.println(e.endNode.ID + "is my neighboor");
					send(new LcrMessage(uin), e.endNode);
					break;
				}
			}
		}	
	}

	@Override
	public void init() {
	    Random rand = sinalgo.tools.Tools.getRandomNumberGenerator();
	    myUin = rand.nextInt(MAX_UIN); 
		System.out.println(this + " is initialized. UIN = " + myUin);
		new MessageTimer(new LcrMessage(myUin)).startRelative(1, this);
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
