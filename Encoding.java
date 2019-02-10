import java.util.Random;

public abstract class Encoding 
{
// Generic Encoding utilities and base class for other encodings 

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

    
protected Codespace cs;
public static Random rand=new Random(); // Insert number for fixed seed - debugging
static String nL = System.getProperty("line.separator");

Encoding()                 { cs=new Codespace(); }
Encoding(Codespace cs)     { this.cs=cs; } 
// Does not make a new one - speed, and Codespaces should be invariant

abstract public String encode(String PT);
abstract public String decode(String CT);

// ----------------------------------------------------------------------
public boolean multiTest()
{
boolean result=true;
String [] tests={"THERAININSPAINFALLSMAINLYONTHEPLAIN","ANIMALVEGETABLEORMINERAL",
    "ITISAFARFARBETTERTHINGIDOTODAYTHANIHAVEEVERQUITEGOTROUNDTODOINGBEFORE",
    "STHISQTIMETHEREWASJUSTTHEDEADEART",
    "THISTIMETHEREWOULDBENOWITNESSESSTHISQTIMETHEREWASJUSTTHEDEADEART"};

for (String s : tests)
  result&=cycleTest(s);

return result;
}
// ----------------------------------------------------------------------
public boolean cycleTest(String text)
{
String CT=encode(text);
String PT=decode(CT);  // Use substring to avoid nulls giving false negs
if (!PT.substring(0,text.length()).equals(cs.flattenToPT(text))) {
  System.out.println("*** FAILED cycle test 1 "+nL+PT+nL+cs.flattenToPT(text));
  return false;
}
// Do not try in reverse (decode then encode) as some ciphers can't decode
// text not encoded by themselves (e.g. Playfair cannot see paired letters)
return true;
}
// ----------------------------------------------------------------------
public boolean knownTestEncode(String PT,String CT)
{
String text=encode(PT);
if (!CT.equals(text)) {
  System.out.println("*** FAILED known test for CT");
  System.out.println("Try : "+text);
  System.out.println("CT  : "+CT);
  return false;
}
return true;
}
// ----------------------------------------------------------------------
public boolean knownTestDecode(String PT,String CT)
{
String text=decode(CT);
if (!PT.equals(text)) {
  System.out.println("*** FAILED known test for PT");
  System.out.println("Try : "+text);
  System.out.println("PT  : "+PT);
  return false;
}
return true;
}
// ----------------------------------------------------------------------
public boolean knownTest(String PT,String CT) 
{ 
boolean pass=knownTestEncode(PT,CT);
reset();
return (knownTestDecode(PT,CT) && pass);  // Do in this order, so both tried.
}
// ----------------------------------------------------------------------
public void reset() { return ;}   // Dummy to be overridden.  
//  Reset encodings/ciphers having an internal state to initialisation value
// ----------------------------------------------------------------------
protected long factorial(int n)                          // Minor utility
{ // Only for fairly small numbers
long result=1;
for (long i=2;i<=n;i++)
  result*=i;
return result;
}
// ----------------------------------------------------------------------
public int[] histogram(String text)                      // Minor utility
{  // Returns histogram of free letters
int [] histogram=new int[cs.PTspace.length()];

for (int i=0;i<text.length();i++) 
  histogram[cs.PTspace.indexOf(text.charAt(i))]++;

return histogram;
}
// ----------------------------------------------------------------------
public static String reverse(String word) {              // Minor utility
       return new StringBuilder(word).reverse().toString();
}
// ----------------------------------------------------------------------
public String expand(String text) 
{  // Converts/expands CT into full codespace alphabet
// N.B. Uses randomness - only really relevant for ciphers

StringBuilder sb=new StringBuilder("");
for (int i=0;i<text.length();i++) {
  int c=cs.CTmap.length()-cs.CTmap.replace(text.substring(i,i+1),"").length();
  if (c==1) sb.append(cs.codeSpace.charAt(cs.CTmap.indexOf(text.charAt(i))));
  else {
    int done=0;
    int tgt=rand.nextInt(c);
    for (int j=0;j<cs.CTmap.length();j++) 
      if (cs.CTmap.charAt(j)==text.charAt(i))
        if (done++ == tgt)
          { sb.append(cs.codeSpace.charAt(cs.CTmap.indexOf(text.charAt(j)))); break; }
  }
}
return sb.toString();
}
// ----------------------------------------------------------------------
public String toString() {  return (nL+cs); }
}