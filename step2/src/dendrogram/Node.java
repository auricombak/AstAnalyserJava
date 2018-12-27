package dendrogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node<T> 
{
    private final T contents;
    private final Double value;
    private final List<Node<T>> children;

    public Node(T contents)
    {
        this.contents = contents;
        this.children = Collections.emptyList();
        this.value = null;
    }

    public Node(Node<T> child0, Node<T> child1, Double value)
    {
        this.contents = null;
        this.value = value;
        List<Node<T>> list = new ArrayList<Node<T>>();
        list.add(child0);
        list.add(child1);
        this.children = Collections.unmodifiableList(list);
    }

    public T getContents()
    {
        return contents;
    }

    public List<Node<T>> getChildren()
    {
        return Collections.unmodifiableList(children);
    }
    
    public Double getValue() {
    	return this.value;
    }
}
