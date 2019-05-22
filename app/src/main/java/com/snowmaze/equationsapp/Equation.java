package com.snowmaze.equationsapp;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

class ListTypeConverter {

    @TypeConverter
    public static ArrayList<Double> stringToArrayList(String json) {
        Type listType = new TypeToken<ArrayList<Double>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public static String arrayListToString(ArrayList<Double> list) {
        Gson gson = new Gson();
        String string = gson.toJson(list);
        return string;
    }
}

class HashMapTypeConverter {

    @TypeConverter
    public static HashMap<Integer, Integer> stringToMap(String json) {
        Type listType = new TypeToken<HashMap<Integer, Integer>>() {}.getType();
        return new Gson().fromJson(json, listType);
    }

    @TypeConverter
    public static String mapToString(HashMap<Integer, Integer> list) {
        Gson gson = new Gson();
        String string = gson.toJson(list);
        return string;
    }
}


@TypeConverters({ListTypeConverter.class, HashMapTypeConverter.class})
@Entity
public class Equation {

     @PrimaryKey(autoGenerate = true)
     int id = 0;
     int cl = 0;
     HashMap<Integer, Integer> st = new HashMap<>();
     private ArrayList<Double> roots = new ArrayList<>();
     String sc = "";
     String scs = "";
     String eq;
     int type = 0;
     int m = 0;
     int b = 0;
     int power = 0;

     Equation(String eq) {
        this.eq = eq;
    }

    private void pl() {
        if (b == 1) {
            if (m == 1) {
                int j = Integer.parseInt(sc);
                if(type == 0) {
                    cl += j;
                }
                else {
                    cl -= j;
                }
                sc = "";
            }
            else {
                int c = 0;
                if(type == 0) {
                    if (sc.equals("")) {
                        c = 1;
                    } else if (sc.equals("-")) {
                        c = -1;
                    } else {
                        c = Integer.parseInt(sc);
                    }
                }
                else {
                    if (sc.equals("")) {
                        c = -1;
                    } else if (sc.equals("-")) {
                        c = 1;
                    } else {
                        c = -Integer.parseInt(sc);
                    }
                }
                int f = 0;
                if (scs.equals("")) {
                    f = 1;
                } else {
                    f = Integer.parseInt(scs);
                }
                if (f > power) {
                        power = f;
                    }
                try {
                        st.put(f, st.get(f) + c);
                    } catch (NullPointerException e) {
                        st.put(f, c);
                    }
                }
                m = 0;
                sc = "";
                scs = "";
            }
     }
     private double[] solveSquare(int a, int b, double c) {
         double roots[] = new double[2];
         double D = b*b - (4*a*c);
         if(D<0) {
             return roots;
         }
         roots[0] = (-b-Math.sqrt(D))/(2*a);
         if(D>0) {
             roots[1] = (-b + Math.sqrt(D)) / (2 * a);
         }
         return roots;
     }
    public void parse() throws Exception {

        for (int i = 0; i < eq.length(); i++) {
            char s = eq.charAt(i);
            if (Character.isDigit(s)) {
                b = 1;
                if(m == 3) {
                    scs += s;
                }
                else {
                    sc += s;
                    m = 1;
                }
            }
            else if(s == 'x') {
                m = 2;
                b = 1;
            }
            else if(s == '^') {
                m = 3;
            }
            else if(s == '+') {
                pl();
                m = 4;
            }
            else if(s == '-') {
                pl();
                m = 4;
                sc += "-";
            }
            else if(s == '>') {
                if(m == 7) {
                    type = 3;
                }
                else {
                    type = 2;
                }
                m = 5;

            }
            else if(s == '<') {
                if(m == 7) {
                    type = 3;
                }
                else {
                    type = 2;
                }
                m = 6;
            }
            else if(s == '=') {
                pl();
                if(type == 2) {
                    if(m == 5) {
                        type = 4;
                    }
                    else {
                        type = 5;
                    }
                }
                else {
                    type = 1;
                    m = 7;
                }
            }
        }
        pl();
        if(power == 0) {
            throw new Exception("Не уравнение");
        }
        if(power == 1) {
            int a = st.get(1);
            if(a==0) {
                throw new Exception("Не уравнение");
            }
           roots.add(-cl/(st.get(1) + 0d));
        }
        else if(power == 2) {
            int a = st.get(2);
            int b = 0;
            try {
                b = st.get(1);
            }
            catch (Exception e) {

            }
            for(double d: solveSquare(a,b,cl)) {
                roots.add(d);
            }
        }
        else if(power == 3) {
            int a = st.get(3);
            int b = 0;
            int c = 0;
            try {
                b = st.get(2);
            }
            catch(Exception e) {}
            try {
                c = st.get(1);
            }
            catch (Exception e) {}
            if(cl == 0) {
                roots.add(0d);
                for(double d: solveSquare(a,b,c)) {
                    roots.add(d);
                }
            }
            else if(cl != 0 && b == 0 && c == 0) {
                roots.add(Math.cbrt((-cl/(a+0d))));

            }
        }
        else if(power == 4) {
            int a = st.get(4);
            int b = 0;
            int c = 0;
            int d = 0;
            try { b = st.get(3); } catch(Exception e) {}
            try { c = st.get(2); } catch (Exception e) {}
            try { d = st.get(1); } catch (Exception e) {}
            //x^4+4x^2 - 21 = 0
            //y^2 + 4y - 21 = 0
            if(b == 0 && d == 0) {
                for(double f: solveSquare(a,c,cl)) {
                    for(double g: solveSquare(1,0, f)) {
                        roots.add(g);
                    }
                }
            }
        }
    }

    public ArrayList<Double> getRoots() {
         return roots;
    }
    public void setRoots(ArrayList<Double> roots) {
         this.roots = roots;
    }

    public HashMap<Integer, Integer> getCoeffs() {
         return st;
    }

    public String getEquation() {
         return eq;
    }
}
