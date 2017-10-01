package com.long3f.views;

/**
 * Created by Admin on 23/9/2017.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class GLListEffect {
    public static GLListEffect instance;
    private final ArrayList<C3045a> f10398b = new ArrayList();

    public interface C3045a {
        void mo2570a(int i, HashMap<Integer, Integer> hashMap);
    }

    public static GLListEffect m16558a() {
        if (instance == null) {
            instance = new GLListEffect();
        }
        return instance;
    }

    public void m16569i(int i) {
        HashMap hashMap = new HashMap();
        hashMap.put(Integer.valueOf(8194), Integer.valueOf(i));
        synchronized (this.f10398b) {
            Iterator it = this.f10398b.iterator();
            while (it.hasNext()) {
                ((C3045a) it.next()).mo2570a(4119, hashMap);
            }
        }
    }



}

