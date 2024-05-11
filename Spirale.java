import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Date;

class Spirale extends ClassicalCipher {
// This was a MTC3 challenge Cipher
// Note inconsistent use of 0 and 1 base for keys - partially due to similar in design

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

static final int KLEN=7;

final static boolean TESTING=false;
protected String [] key;
int [] pi1;
int [] ip1; // Inverse of pi
int [] pi2;
int [] ip2; // Inverse of pi2
int [] lk;

// ----------------------------------------------------------------------
Spirale(String k1,String k2,String k3,String k4) 
{
super(new Codespace(Codespace.StockAlphabet.CAPITALS));
key=new String[4];

if (k1==null) { // Random - not completely elegant, prefer Spirale() constructor
  for (int i=0;i<4;i++) {
    StringBuilder sb=new StringBuilder(7);
    for (int j=0;j<7;j++)
      sb.append((char)(65+rand.nextInt(26)));
    key[i]=sb.toString();
  }
  k1=key[0];
  k2=key[1];
  k3=key[2];
  k4=key[3];
}
else {
  key[0]=new String(k1);
  key[1]=new String(k2);
  key[2]=new String(k3); 
  key[3]=new String(k4);
}

ip1=permed(k1);
pi1=new int[26];
for (int i=0;i<26;i++)
  pi1[ip1[i]]=i;

ip2=permed(k2);
pi2=new int[26];
for (int i=0;i<26;i++)
  pi2[ip2[i]]=i;

lk=new int[49];  // Create 'long key'
int i=0;
int j=0;
for (int z=0;z<49;z++) {
  
  lk[z]=(pi1[(int)k3.charAt(j)-65]+pi2[(int)k4.charAt(i)-65])%26; // j, i in that order because k4 across top of table
    
  if (i==6) {
    i=j+1;
    j=6;
  } else if (j==0) {
    j=i+1;
    i=0;
  }
  else { j--; i++; }
}
}
// ----------------------------------------------------------------------
Spirale(int [] k1,int [] k2,int [] k3,int [] k4) 
{
super(new Codespace(Codespace.StockAlphabet.CAPITALS));
key=new String[4];

/* // Should convert to string, but does not affect operation EXCEPT perturb
 setkey(k1,k2,k3,k4);
*/
ip1=permed(k1);
pi1=new int[26];
for (int i=0;i<26;i++)
  pi1[ip1[i]]=i;

ip2=permed(k2);
pi2=new int[26];
for (int i=0;i<26;i++)
  pi2[ip2[i]]=i;

lk=new int[49];  // Create 'long key'
int i=0;
int j=0;
for (int z=0;z<49;z++) {
  
  lk[z]=(pi1[k3[j]]+pi2[k4[j]])%26; // j, i in that order because k4 across top of table
    
  if (i==6) {
    i=j+1;
    j=6;
  } else if (j==0) {
    j=i+1;
    i=0;
  }
  else { j--; i++; }
}
}
// ----------------------------------------------------------------------
public Spirale copyInstance(ClassicalCipher c) // Copy factory method
  { return new Spirale(((Spirale)c).key[0],((Spirale)c).key[1],
                       ((Spirale)c).key[2],((Spirale)c).key[3]); }
// ----------------------------------------------------------------------
protected void setkey(int [] k1,int [] k2,int [] k3,int [] k4) 
{ // Only for cosmetic purposes
StringBuilder sb=new StringBuilder(10);
for (int j=0;j<7;j++) {
  sb.append((char)(65+k1[j]));   
}
key[0]=sb.toString();

sb=new StringBuilder(10);
for (int j=0;j<7;j++) {
  sb.append((char)(65+k2[j]));   
}
key[1]=sb.toString();
sb=new StringBuilder(10);
for (int j=0;j<7;j++) {
  sb.append((char)(65+k3[j]));   
}
key[2]=sb.toString();

sb=new StringBuilder(10);
for (int j=0;j<7;j++) {
  sb.append((char)(65+k4[j]));   
}
key[3]=sb.toString();
}
// ----------------------------------------------------------------------
public static int [] permed(String s) 
{  // When starting with a string of length 7 (the key) convert to a 7
   // index array, and pass that array to permed(int []) to return the
   // 26-index permutation.

if (s.length()!=KLEN) throw new IllegalArgumentException("Length !=7");
// Comment out for speed, if you're sure

int [] perm=new int[KLEN];
for (int i=0;i<KLEN;i++)
  perm[i]=s.charAt(i)-64;

return permed(perm);
}
// ----------------------------------------------------------------------
public static int [] permed(int [] perm) // perm is 1-based
{ // When starting with a 7 index int[], convert to the 26 index permutation array

int index=0;
int place=26;
int [] result=new int[26];
boolean [] used=new boolean[26];

for (int i=0;i<26;i++) {
  for (int j=0;j<perm[i%KLEN];) { // Increment inside loop
    place--;
    if (place<0) place=25;
    if (!used[place]) {
      j++;
    }
  }  
  used[place%26]=true;
  result[index++]=place%26;
}
return result;
}
// ----------------------------------------------------------------------
public String toString() 
     { return (" "+key[0]+
       " "+key[1]+" "+key[2]+" "+key[3]);}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) 
{
int [] stream=new int[PT.length()];
for (int i=0;i<49 && i<PT.length();i++)
  stream[i]=lk[i];
for (int i=49;i<PT.length();i++)
  stream[i]=(pi1[stream[i-49]]+pi2[stream[i-24]])%26;

StringBuilder sb=new StringBuilder(PT.length());
for (int i=0;i<PT.length();i++) 
  sb.append((char)(65+(pi1[PT.charAt(i)-65]+pi2[stream[i]])%26));

return sb.toString(); 
} 
// ----------------------------------------------------------
@Override
public String decode(String CT) 
{
int [] stream=new int[CT.length()];
for (int i=0;i<49 && i<CT.length();i++)
  stream[i]=lk[i];
for (int i=49;i<CT.length();i++)
  stream[i]=(pi1[stream[i-49]]+pi2[stream[i-24]])%26;

StringBuilder sb=new StringBuilder(CT.length());
for (int i=0;i<CT.length();i++) 
  sb.append((char)(65+ip1[(CT.charAt(i)-65-pi2[stream[i]]+52)%26]));

return sb.toString(); 
}
// ----------------------------------------------------------
public static void main(String [] args) {

// Example from MTC3 Spirale Description
String PT="SPIRALEISAONETIMEPADCRYPTOSYSTEMDESIGNEDTOREPLACESOLITAIREWHENONEHASNOCARDS";
String CT="HXYYEQXLUFBJQLAHYTYMHXONCHQKYEAWSJRRREUQQWNKGIUNWNMTRSPDXFONSMCJHAEDFKZQAFL";

Spirale spirale=new Spirale("NVIKKIH","CTSQEOU","DNGDKSZ","EAIWDSH");

if (spirale.knownTest(PT,CT)) System.out.println("PASS");
else                          System.out.println("***** FAIL *******");
}
}
