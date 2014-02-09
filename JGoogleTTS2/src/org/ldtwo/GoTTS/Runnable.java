/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.ldtwo.GoTTS;

/**
 *
 * @author ldtwo
 */
public abstract class Runnable implements java.lang.Runnable{

    @Override
    public void run() {
        try{run_();}catch(Exception e){e.printStackTrace();}
    }
    public abstract void run_();
}
