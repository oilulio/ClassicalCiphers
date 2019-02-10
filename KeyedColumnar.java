import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class KeyedColumnar extends ClassicalCipher
{
// Conducts KeyedColumnar encryption and decryption.

/*
Copyright (C) 2019  S Combes

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>. */

public int[] key;    // The order of the columns

KeyedColumnar(KeyedColumnar from)    // Copy constructor
{ 
this.cs=from.cs;
this.key=new int[from.key.length];
for (int i=0;i<from.key.length;i++)
  this.key[i]=from.key[i];
}
// ----------------------------------------------------------------------
KeyedColumnar(String word)
  { this(new Codespace(Codespace.StockAlphabet.CAPITALS),word); }
// ----------------------------------------------------------------------
KeyedColumnar(Codespace cs,String word)
{
super(cs);
String skey=cs.flattenToPT(word); 
setKey(oneorderit(skey));  
}
// ----------------------------------------------------------------------
KeyedColumnar(int [] order)
{
this(new Codespace(Codespace.StockAlphabet.CAPITALS),order);
setKey(order);
}
// ----------------------------------------------------------------------
KeyedColumnar(Codespace cs,int [] order)
{
super(cs);
setKey(order);
}
// --------------------------------------------------------------
private String showKey() {

int [] inv=invert(key);
StringBuffer sb=new StringBuffer(100);
for (int i=0;i<key.length;i++)
  sb.append(inv[i]+" ");

return sb.toString();
}
// --------------------------------------------------------------
public String toString() 
     { return ("Keyed Columnar Cipher : Columns="+key.length+" Key="+showKey()+
               super.toString());}
// ----------------------------------------------------------------------
protected void setKey(int[] order) 
{ 
// Sets key by sorting out duplicates into order, e.g. [0,2,2,1] keys as [0,2,3,1].
// and inverting to form where k[i] indicates the ith column to read from.

// Does not cope with keys not specified with consecutive numbers starting from 0 or 1 

key=new int[order.length];

int place=0;
for (int i=0;i<=order.length;i++) // '<=' to cope with 1,2,...n case
  for (int j=0;j<order.length;j++) 
     if (order[j]==i) 
       key[place++]=j;

if (place!=order.length) 
  throw new IllegalArgumentException("Key needs consecutive numbers from 0 or 1");

int sum=0;
for (int k : key) sum+=k;

if (sum!=((place*(place-1))/2)) 
  throw new IllegalArgumentException("Key did not lead to permutation");
}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) { return byCols(cs.flattenToPT(PT),key); }
// ----------------------------------------------------------------------
@Override
public String decode(String CT) { return invertByCols(cs.flattenToCT(CT),key); }
// ----------------------------------------------------------------------
public static void main(String [] args)
{  
//http://en.wikipedia.org/w/index.php?title=Transposition_cipher&oldid=571502098
String pt="WEAREDISCOVEREDFLEEATONCE"; 
int [] order={6,3,2,4,1,5};
KeyedColumnar kc=new KeyedColumnar(order);

//System.out.println(kc);
if (kc.knownTest(pt,"EVLNACDTESEAROFODEECWIREE")) System.out.println("PASS");
else System.out.println("******** FAIL ***********");
}
}