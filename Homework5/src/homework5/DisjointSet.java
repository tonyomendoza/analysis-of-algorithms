package homework5;

/**
 *
 * @author Tony Mendoza Some code borrowed from tutorial:
 * http://www.geeksforgeeks.org/union-find/ Other code borrowed from my older
 * class.
 *
 */
public class DisjointSet<E> {

    private int size; /// How many elements in the ArrayList currently present
    private int capacity; // Total space of the array
    private E[] myArray;
    public int[] parent; // points to parent
    private static final int INITIAL_CAPACITY = 10;

    // Overloaded constructor to create ArrayList of variable size
    public DisjointSet() {
        this.capacity = INITIAL_CAPACITY; // defualt capacity
        this.size = 0; // No elements at the beginning
        myArray = (E[]) new Object[this.capacity];
        parent = new int[this.capacity];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }
    }

    // Constructor with defined capacity
    public DisjointSet(int capacity) {
        this.capacity = capacity;
        this.size = 0;
        myArray = (E[]) new Object[this.capacity];
        parent = new int[this.capacity];
        for (int i = 0; i < parent.length; i++) {
            parent[i] = -1;
        }
    }

    // Add an object to an arraylist
    public void add(E o) {
        /// This method adds the data at the end of the ArrayList
        if (size < capacity) { // Tehre is space in the ArrayList to add elements
            myArray[size] = o;
            size++;
        } else {
            System.out.println("Not enough space. Reallocate Called.");
            this.reallocate();
            this.add(o);
        }
    }

    private void reallocate() {
        this.capacity *= 2; // Double the capacity for the new array
        E[] temp = (E[]) new Object[this.capacity];
        int[] parentTemp = new int[this.capacity];
        // Need to copy the old data into the new array
        for (int i = 0; i < myArray.length; i++) {
            temp[i] = myArray[i];
            parentTemp[i] = parent[i];
        }
        for (int i = myArray.length; i < temp.length; i++) {
            parentTemp[i] = -1;
        }
        // Need to point the reference of the array to the correct data
        this.myArray = temp;
        this.parent = parentTemp;
    }

    public void add(E o, int index) {
        /// This method adds the data at the given point of the ArrayList
        if (size >= capacity) {
            System.out.println("\nNot enough space. Reallocate Called.");
            this.reallocate();
        }

        E[] temp = (E[]) new Object[this.capacity];
        // Need to copy the old data into the new array
        for (int i = 0; i < myArray.length; i++) {
            if (i < index) {
                temp[i] = myArray[i];
            } else if (i > index && i - 1 < size) {
                temp[i] = myArray[i - 1];
            } else {
                temp[i] = o;
            }
        }
        this.myArray = temp;
        size++;
    }

    public void remove(int index) {
        // This method removes data from the array
        // Check if the index is valid or not
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid Index: " + index);
        } else {
            E[] temp = (E[]) new Object[this.capacity];
            // Need to copy the old data into the new array
            for (int i = 0; i < myArray.length; i++) {
                if (i < index) {
                    temp[i] = myArray[i];
                } else if (i > index && i < size) {
                    temp[i - 1] = myArray[i];
                }
            }
            this.myArray = temp;
            size--;
        }
    }

    public Object get(int index) {
        // Returns the data at the given index
        // Check if the index is valid or not
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid Index: " + index);
        } else {
            return myArray[index];
        }
    }

    public void set(int index, E o) {
        // Sets the data at the given index
        // Check if the index is valid or not
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Invalid Index: " + index);
        } else {
            myArray[index] = o;
        }
    }

    public int getSize() {
        // Return the "physical" size of the array
        return size;
    }

    public int indexOf(E o) {
        // Loop through the array until the object is found, then return its index. 
        for (int i = 0; i < size; i++) {
            if (o == myArray[i]) {
                return i;
            }
        }
        return -1; // return -1 if not found
    }

    public void display() {
        /// this method displays the content of hte ArrayList
        System.out.print("Displaying list: ");
        for (int i = 0; i < size; i++) {
            System.out.print(myArray[i]);
            if (i < size - 1) {
                System.out.print(", ");
            }
        }
        System.out.print("\n");
    }

    public boolean contains(E object) {
        for (int i = 0; i < myArray.length; i++) {
            if (myArray[i].equals(object)) {
                return true;
            }
        }
        return false;
    }
    
    // sorting method

    /// THE FOLLOWING WAS ADDED FROM THE TUTORIAL
    
    public int find(E o) {
        int i = indexOf(o);
        if (parent[i] == -1) {
            return i;
        }
        return find(i);
    }
    
    int find(int i) {
        if (parent[i] == -1) {
            return i;
        }
        return find(parent[i]);
    }

    void union(E x, E y) {
        int xSet = find(x);
        int ySet = find(y);
        parent[xSet] = ySet;
    }
}