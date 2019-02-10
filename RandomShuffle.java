public class RandomShuffle extends ClassicalCipher
{  //  Not a cipher, just a helpful function
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
  
// ---------------------------------------------------------------------------
RandomShuffle()  { this (new Codespace(Codespace.StockAlphabet.CAPITALS)); }
RandomShuffle(Codespace cs)  
{ 
super(cs); 
if (!cs.PTspace.equals(cs.CTspace)) throw new IllegalArgumentException(
   "Shuffle must have identical character spaces for PT and CT");
}
// ---------------------------------------------------------------------------
@Override
public String encode(String text) { return shuffle(cs.flattenToPT(text)); }
// ---------------------------------------------------------------------------
@Override
public String decode(String text) { return encode(text); } // Codespaces are same
// ---------------------------------------------------------------------------
public static String shuffle(String text) {
// The static equivalent

StringBuilder sb=new StringBuilder(text.length());
int [] items=randomPermutation(text.length());

for (int i=0;i<items.length;i++)
  sb.append(text.charAt(items[i]));

return sb.toString();
}
// ---------------------------------------------------------------------------
public static int [] randomPermutation(int n)  // Public - has other uses.
{ 
// Fischer-Yates Shuffle ('inside-out' algorithm)
// http://en.wikipedia.org/w/index.php?title=Fisher%E2%80%93Yates_shuffle&oldid=597499563

int [] items=new int[n];

items[0]=0;

for (int i=1;i<n;i++) {
  int j=rand.nextInt(i+1);  // i correct, not n.  +1 because nextInt is exclusive for end index
  if (j!=i) items[i]=items[j];
  items[j]=i;
}
return items;
}
// ---------------------------------------------------------------------------
public static void main(String [] args) {

RandomShuffle rs=new RandomShuffle();

for (int i=0;i<10;i++)
  System.out.println(rs.encode("ABCDEF"));

String test="ABCDEFGHIJKLMNO";
int [] freq=new int[test.length()];
for (int i=0;i<10000;i++) {
  String out=rs.encode(test);
  for (int j=0;j<test.length();j++)
    if (test.indexOf(out.charAt(j))==j)
      freq[j]++;
}
for (int i=0;i<freq.length;i++)
  System.out.println(i+" "+freq[i]);
System.out.println("Distribution should be uniform (statistically)");
}
}