/*
import static org.junit.Assert.*;

import org.junit.Test;

public class unionTest {



    private  void union(int[]a, int x, int y){
        a[find(a,y)]=find(a,x);
    }

    public int find(int[] a, int id) {
        return a[id]==id ? id : find(a,a[id]);
    }

    int[] a;
    @Test
    public void test() {
        a = new int[5];
        for (int i=0; i<a.length; i++){
            a[i] = i;
            System.out.println(a[i]);
        }

        for(int i=0; i<=a.length; i++){
            union(a, a[i], a[i+1]);
            System.out.println(find(a,a[i]));

        }
        //System.out.println(a[3]);
        assertEquals(0,find(a,a[2]));
    }

}*/
