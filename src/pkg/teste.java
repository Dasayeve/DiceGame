/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg;

/**
 *
 * @author dasax
 */
public class teste {

    public static void main(String[] args) {
        String returnMessage="1#2@3";
        int point1 = Integer.parseInt((returnMessage.split("#")[1]).split("@")[0]);
        int point2 = Integer.parseInt((returnMessage.split("#")[1]).split("@")[1]);
        System.out.println(point2);
    }
}
