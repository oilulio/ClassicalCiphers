import java.util.Date;
import java.util.*;

public class StraddlingCheckerboard extends ClassicalCipher
{
// Conducts StraddlingCheckerboard cipher encryption and decryption.

// Where the number of characters in the Straddling Checkerboard
// does not fully fill the array, then the Checkerboard cannot
// safely be used with a reverse encryption AFTER a second cipher
// stage (such as seen in VIC).  Has function boolean safeReencrypt()
// to indicate if this action is safe.

// Reason is that some numerical transformation of the output can
// create the need to use one of the un-defined positions in the 
// array.

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

protected String key;
protected int [] omit; // List of decimal digits omitted in 1st line
protected boolean [] omitted;  // Whether digit is omitted 
protected int []   mapping0;   // Mappings of top (0th) line, blanks as -1
protected int [][] mappingN;   // Mappings of remaining lines, including unused.
protected String [] gnippam;   // Reverse mapping

// ----------------------------------------------------------------------
StraddlingCheckerboard(String key,int [] omit) 
{ 
super(new Codespace(key,key,"0123456789","0123456789"));

if (Codespace.uniqueLength(key)!=key.length())
  throw new IllegalArgumentException("Key must be unique charactes");

if (key.length()>(10*omit.length+10-omit.length))
  throw new IllegalArgumentException("Key too long");

this.key=new String(key);
omitted=new boolean[10];
mapping0=new int[10];  
mappingN=new int[10][10];  // Store mostly empty array for speed/ease
gnippam=new String[key.length()];

this.omit=new int[omit.length];
for (int i=0;i<omit.length;i++) {
  if (omit[i]<0 || omit[i]>9)
    throw new IllegalArgumentException("Omissions must be numeric 0-9");
  this.omit[i]=omit[i];
  omitted[omit[i]]=true;
}

int index=0;
for (int j=0;j<10;j++) {
  if (omitted[j]) 
    mapping0[j]=-1;
  else {          
    mapping0[j]=index;
    gnippam[index++]=Character.toString((char)('0'+j));
  }
}
for (int i=0;i<omit.length;i++) { 
  for (int j=0;j<10;j++) {
    if (index<key.length()) { 
      gnippam[index]=Character.toString((char)('0'+omit[i]))+Character.toString((char)('0'+j));
      mappingN[omit[i]][j]=(index++);
    }
    else 
      mappingN[omit[i]][j]=-1;
  }
}
}
// ----------------------------------------------------------------------
public String toString() 
{
StringBuilder sb=new StringBuilder(
      "Straddling Checkerboard Cipher : Key is:"+nL);

sb.append(nL+"  0123456789"+nL+"-:");
for (int j=0;j<10;j++) {
  if (mapping0[j]>=0)  sb.append(key.charAt(mapping0[j]));
  else                 sb.append('*');
}

for (int i=0;i<mappingN.length;i++) {
  if (omitted[i]) {
    sb.append(nL+(char)(i+'0')+":");

    for (int j=0;j<10;j++) {
      if (mappingN[i][j]>=0) sb.append(key.charAt(mappingN[i][j]));
      else                   sb.append('*');
    }
  }
}

if (safeReencrypt())
  sb.append(nL+"Can be re-encrypted (used with transposition)");

return (sb.toString()+super.toString());
}
// ----------------------------------------------------------------------
public boolean safeReencrypt() {
  
int rows=omit.length+1;
int characters=(rows*10)-omit.length;
return (characters==key.length());
}
// ----------------------------------------------------------
@Override
public String encode(String PT) 
{ 
String flat=cs.flattenToPT(PT);

StringBuilder sb=new StringBuilder();

for (int i=0;i<flat.length();i++) {
  sb.append(gnippam[key.indexOf(flat.charAt(i))]);
}
return sb.toString(); 
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{ 
String flat=cs.flattenToCT(CT);

StringBuilder sb=new StringBuilder();

for (int i=0;i<flat.length();i++) {
  int init=flat.charAt(i)-'0';
  if (!omitted[init]) {
    sb.append(key.charAt(mapping0[init]));
  }  
  else {
    sb.append(key.charAt(mappingN[init][flat.charAt(++i)-'0']));
  }
}
return sb.toString(); 
}
// ----------------------------------------------------------
public static void main(String [] args) {

// Test from http://crypto.interactive-maths.com/straddling-checkerboard.html
int [] omit={1,7};
Keyword kw=new Keyword().new Simple(new Codespace("ABCDEFGHIJKLMNOPQRSTUVWXYZ #"),"ETAOINSRF HKGBCDJL");
StraddlingCheckerboard sc=new StraddlingCheckerboard(kw.getKey(),omit);

if (sc.multiTest() && sc.knownTest("ATTACK AT DAWN","32231613113211173756"))
 System.out.println("PASS");
else
  System.out.println("***** FAIL *******");
}
}