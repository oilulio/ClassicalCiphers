public class Solitaire extends ClassicalCipher 
{
// Implements Bruce Schneier's Solitaire/Pontiflex algorithm
// See https://www.schneier.com/solitaire.html
// NB.  Uses 0 basing throughout (i.e. Ace of Clubs = 0, JOKER_B=53)
// Schneier tends to use 1-54.

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

protected int [] after;    // The encryption mappings 
protected int [] before;
protected int top;
public static final int JOKER_A=52;
public static final int JOKER_B=53;
private static String [] suit = {"C","D","H","S"};
private static String [] rank = {"A","2","3","4","5","6","7","8","9","10","J","Q","K"};

// ----------------------------------------------------------------------
Solitaire(int [] ordering)  
{ 
super(new Codespace(Codespace.StockAlphabet.CAPITALS)); 
after=new int[54];
before=new int[54];

if (ordering.length!=54) throw new 
        IllegalArgumentException("Invalid length of ordering");

boolean [] present=new boolean[54];
for (int i=0;i<54;i++) {
  if (ordering[i]<0 || ordering[i]>=54) throw new 
        IllegalArgumentException("Invalid value in ordering");
  present[ordering[i]]=true;
}

for (int i=0;i<54;i++) 
  if (!present[i]) throw new 
        IllegalArgumentException("Ordering is not full set");

for (int i=0;i<54;i++) {
  if (ordering[i]==0)  top=i;
  for (int j=0;j<54;j++) {
    if (ordering[j]==(ordering[i]+1)%54) {
      after[i]=j;
      before[j]=i;
    }
  }
}
}  
// ----------------------------------------------------------------------
boolean consistent() { // For debugging

boolean pass=true;
boolean [] present=new boolean[54];
for (int i=0;i<54;i++) {
  if (before[after[i]]!=i) {
    pass=false;
    System.out.println(i+" has "+after[i]+" after, which has "+before[after[i]]+" before");
  }
  present[after[i]]=true;
}
for (int i=0;i<54;i++)
  if (!present[i])
    pass=false;

return pass;
}
// ----------------------------------------------------------------------
private void step1() 
{
// Step 1 : Move Joker A down 1 card, but if it is last card, move to be second.

  int bottom=before[top];

  if (bottom==JOKER_A) {

    bottom=before[JOKER_A];
    after[bottom]=top;
    before[JOKER_A]=top;
    after[JOKER_A]=after[top];
    before[after[JOKER_A]]=JOKER_A;
    after[top]=JOKER_A;
    before[top]=bottom;
  } 
  else {
    int a=after[JOKER_A];
    int b=before[JOKER_A];

    after[b]=a;
    after[JOKER_A]=after[a];
    after[a]=JOKER_A;

    before[JOKER_A]=a;
    before[after[JOKER_A]]=JOKER_A;
    before[a]=b;

    if (top==JOKER_A)         // Top pointer needs updatig iff JA was top 
      top=before[JOKER_A];
  }
}
// ----------------------------------------------------------------------
private void step2() 
{
  // Step 2 : Move Joker B down 2 cards, but if it is last card, move to be third.
  // If it was the penultimate, move to be second.

  int bottom=before[top];

  if (bottom==JOKER_B) { 

    bottom=before[JOKER_B];
    after[bottom]=top;
    before[top]=bottom;

    before[JOKER_B]=after[top];
    after[JOKER_B]=after[after[top]];

    before[after[JOKER_B]]=JOKER_B;
    after[before[JOKER_B]]=JOKER_B;
  } 
  else if (before[bottom]==JOKER_B) { 

    after[before[JOKER_B]]=bottom;   
    before[bottom]=before[JOKER_B];

    before[JOKER_B]=top;
    after[JOKER_B]=after[top];
    before[after[JOKER_B]]=JOKER_B;
    after[top]=JOKER_B;
  } 
  else {
    int third=after[after[after[JOKER_B]]];  // What will now follow JB was 3 ahead

    after[before[JOKER_B]]=after[JOKER_B];   // Cut JB out by connecting its
    before[after[JOKER_B]]=before[JOKER_B];  // predecessor and follower
 
    before[JOKER_B]=after[after[JOKER_B]];   // Reinsert JB after a card 2 later
    after[before[JOKER_B]]=JOKER_B;

    after[JOKER_B]=third;                    // and connect it onwards to the
    before[after[JOKER_B]]=JOKER_B;          // previous third card

    if (top==JOKER_B)                        // Top pointer needs updatig iff JB was top 
      top=before[before[JOKER_B]];
  }
}
// ----------------------------------------------------------------------
private void step3() 
{
  // Step 3 : Triple cut.  Identify cards from top to first Joker you come to
  // Swap with cards between last Joker and bottom of pack.

  int bottom=before[top];
 
  int topJoker=top;
  while (topJoker!=JOKER_A && topJoker != JOKER_B)
    topJoker=after[topJoker];

  int bottomJoker=bottom;
  while (bottomJoker!=JOKER_A && bottomJoker!=JOKER_B)
    bottomJoker=before[bottomJoker];

  if (topJoker==bottomJoker) {
    System.out.println("Joker Equality error ****");
    System.exit(0);
  }

  int topTobe=after[bottomJoker];
  if (topTobe==top)  // Bottom section was empty
    topTobe=topJoker;

  after[bottomJoker]=top;
  before[top]=bottomJoker;

  after[before[topJoker]]=topTobe;
  before[topTobe]=before[topJoker];

  before[topJoker]=(topTobe==topJoker)?before[topJoker]:bottom;
  after[bottom]=(topTobe==topJoker)?top:topJoker;

  top=topTobe;
  bottom=before[top];
}
// ----------------------------------------------------------------------
private void step4(int count) 
{
// Step 4 : Count cut.  
// Cut at this point, leaving last card alone. (N.B. Schneier uses 1-53)

int bottom=before[top];

if (bottom!=JOKER_A && bottom!=JOKER_B) {  // Untouched if joker on bottom

  int topTobe=top;
  for (int i=0;i<=count;i++) 
    topTobe=after[topTobe];

  after[before[bottom]]=top;
  before[top]=before[bottom];

  before[bottom]=before[topTobe];
  after[before[topTobe]]=bottom;

  before[topTobe]=bottom;
  after[bottom]=topTobe;

  top=topTobe;
  // bottom is unchanged.
}
}
// ----------------------------------------------------------------------
private int step() 
{
int outputCard;

do {
  step1();
  step2();
  step3();
  // Get number 0-52 of last card (both Jokers = 52)
  step4((before[top]==JOKER_B)?52:before[top]);

  if (!consistent()) { System.out.println("******* INCONSISTENT **********"); System.exit(0); }

  // Step 5 : Get output card

  int count=(top==JOKER_B)?52:top;
  outputCard=after[top];
  for (int i=0;i<count;i++) 
    outputCard=after[outputCard];

} while (outputCard==JOKER_A || outputCard==JOKER_B);

return 1+(outputCard%26);  // '1+' converts to the 1-26 used by Schneier
}
// -----------------------------------------------------------------
public void setKey(String passphrase) 
{
// First set in null order
for (int i=0;i<54;i++) {
  after[i]=(i+1)%54;
  before[(i+1)%54]=i;
}

top=0;
for (int i=0;i<passphrase.length();i++) {
  step1();
  step2();
  step3();
  step4((before[top]==JOKER_B)?52:before[top]);
  step4((int)passphrase.toUpperCase().charAt(i)-65);  // Special step 4
}
return;
}
// --------------------------------------------------------------
public String toString() 
{
StringBuilder sb=new StringBuilder(120);

int card=top;
for (int i=0;i<54;i++) {
  if      (card==JOKER_A) sb.append("JA* ");
  else if (card==JOKER_B) sb.append("JB$ ");
  else sb.append(rank[card%13]+suit[card/13]+" ");
  card=after[card];
}
return (/*super.toString()+nL+*/"Solitaire cipher. Key :"+nL+sb.toString()); 
}
// ----------------------------------------------------------------------
@Override
public String encode(String PT) 
{
String flat=cs.flattenToPT(PT);

StringBuilder sb=new StringBuilder(PT.length());
for (int i=0;i<flat.length();i++)
  sb.append(cs.CTspace.charAt((cs.PTspace.indexOf(flat.charAt(i))+step())%26));
return sb.toString();
}
// ----------------------------------------------------------------------
@Override
public String decode(String CT) 
{
String flat=cs.flattenToCT(CT);

StringBuilder sb=new StringBuilder(CT.length());
for (int i=0;i<flat.length();i++)
  sb.append(cs.PTspace.charAt((cs.CTspace.indexOf(flat.charAt(i))+26-step())%26));
return sb.toString();
}
// ----------------------------------------------------------------------
public static void main(String [] args) {

boolean pass=true;

// Test vectors
// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key: <null key>
// Output: 4 49 10 53 24 8 51 44 6 4 33 20 39 19 34 42 
// Ciphertext: EXKYI ZSGEH UNTIQ 

int [] order = new int[54];
for (int i=0;i<54;i++)
  order[i]=i;

Solitaire sol=new Solitaire(order);
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","EXKYIZSGEHUNTIQ");
sol=new Solitaire(order);
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","EXKYIZSGEHUNTIQ");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key: 'f'
// Output: 49 24 8 46 16 1 12 33 10 10 9 27 4 32 24 
// Ciphertext: XYIUQ BMHKK JBEGY 

sol.setKey("F");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","XYIUQBMHKKJBEGY");
sol.setKey("F");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","XYIUQBMHKKJBEGY");

// ................................................................
// Plaintext: AAAAAAAAAAAAAAA 
// Key: 'fo'
// Output:  19 46 9 24 12 1 4 43 11 32 23 39 29 34 22 
// Ciphertext:  TUJYM BERLG XNDIW

sol.setKey("FO");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","TUJYMBERLGXNDIW");
sol.setKey("FO");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","TUJYMBERLGXNDIW");

// ................................................................
// Plaintext: AAAAAAAAAAAAAAA
// Key: 'foo'
// Output: 8 19 7 25 20 53 9 8 22 32 43 5 26 17 53 38 48 
// Ciphertext: ITHZU JIWGR FARMW 

sol.setKey("FOO");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","ITHZUJIWGRFARMW");
sol.setKey("FOO");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","ITHZUJIWGRFARMW");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key:  'a'
// Output:  49 14 3 26 11 32 18 2 46 37 34 42 13 18 28 
// Ciphertext:  XODAL GSCUL IQNSC 

sol.setKey("A");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","XODALGSCULIQNSC");
sol.setKey("A");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","XODALGSCULIQNSC");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key: 'aa'
// Output:  14 7 32 22 38 23 23 2 26 8 12 2 34 16 15 
// Ciphertext:  OHGWM XXCAI MCIQP 

sol.setKey("AA");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","OHGWMXXCAIMCIQP");
sol.setKey("AA");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","OHGWMXXCAIMCIQP");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key: 'aaa'
// Output:  3 28 18 42 24 33 1 16 51 53 39 6 29 43 46 45 
// Ciphertext: DCSQY HBQZN GDRUT 

sol.setKey("AAA");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","DCSQYHBQZNGDRUT");
sol.setKey("AAA");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","DCSQYHBQZNGDRUT");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key: 'b'
// Output:  49 16 4 30 12 40 8 19 37 25 47 29 18 16 18 
// Ciphertext:  XQEEM OITLZ VDSQS 

sol.setKey("B");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","XQEEMOITLZVDSQS");
sol.setKey("B");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","XQEEMOITLZVDSQS");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key:  'bc'
// Output:  16 13 32 17 10 42 34 7 2 37 6 48 44 28 53 4 
// Ciphertext:  QNGRK QIHCL GWSCE 

sol.setKey("BC");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","QNGRKQIHCLGWSCE");
sol.setKey("BC");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","QNGRKQIHCLGWSCE");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAA
// Key:  'bcd'
// Output:  5 38 20 27 50 1 38 26 49 33 39 42 49 2 35 
// Ciphertext:  FMUBY BMAXH NQXCJ 

sol.setKey("BCD");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAA","FMUBYBMAXHNQXCJ");
sol.setKey("BCD");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAA","FMUBYBMAXHNQXCJ");

// ................................................................
// Plaintext:  AAAAAAAAAAAAAAAAAAAAAAAAA
// Key: 'cryptonomicon'
// Ciphertext:  SUGSR SXSWQ RMXOH IPBFP XARYQ

sol.setKey("cryptonomicon");
pass &=sol.knownTestEncode("AAAAAAAAAAAAAAAAAAAAAAAAA","SUGSRSXSWQRMXOHIPBFPXARYQ");
sol.setKey("cryptonomicon");
pass &=sol.knownTestDecode("AAAAAAAAAAAAAAAAAAAAAAAAA","SUGSRSXSWQRMXOHIPBFPXARYQ");

// ................................................................
// Plaintext: SOLITAIRE
// Key:  'cryptonomicon'
// Ciphertext:  KIRAK SFJAN 

sol.setKey("cryptonomicon");
pass &=sol.knownTestEncode("SOLITAIREX","KIRAKSFJAN"); // Paper did not show null X
sol.setKey("cryptonomicon");
pass &=sol.knownTestDecode("SOLITAIREX","KIRAKSFJAN");

// ................................................................

System.out.println(pass?"PASS":"*** FAILED *******");

}

}