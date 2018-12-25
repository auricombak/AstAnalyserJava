package info;

public class DendroNode implements DendroElt{
	private DendroElt leftChild;
	private DendroElt rightChild;
	private Double weight;
	
	public DendroNode(DendroElt childL, DendroElt childR, Double weight) {
		this.leftChild = childL;
		this.rightChild = childR;
		this.weight = weight;
	}
	
	public DendroElt getRight() {
		return rightChild;
	}
	
	public DendroElt getLeft() {
		return leftChild;
	}
	
	public Double getWeight() {
		return weight;
	}
	public void setWeight(Double weight) {
		this.weight = weight;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "( W : " + weight + " ch1: " + leftChild.getName() + "  ch2:" + rightChild.getName() + ")";
	}
}
