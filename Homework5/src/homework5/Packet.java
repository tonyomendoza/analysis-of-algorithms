/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package homework5;

/**
 *
 * @author Tony Mendoza
 */
public class Packet {
    protected int size;
    
    
    public Packet(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }   
}