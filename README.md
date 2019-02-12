<b>A set of encodings and Classical Ciphers implemented in Java</b>

The main() method of most ciphers runs a test module to confirm the cipher is returning an expected value.
ClassicalCipher provides standard test modules (e.g. provide matching PT,CT pair and checks encrypt and decrypt)
Test methods can be awkward for non-fully specified classical ciphers, e.g. where unknown nulls are added.

Ciphers usually have toString() methods that show their type, codespace and specific key.

Base classes are Encoding and ClassicalCipher.  Provide foundation methods and structure.

Two key helper classes are Codespace and Keyword.

Codespace provides helper functions and defines the plain text alphabet that may be presented to the cipher (e.g. capital letters only); the letters the Cipher recognises at input; and the mappings between the plan text and these letters.  The reverse applies at output.

As an example, many classical ciphers using a square to define their transpositions can only utilise 25 characters of the alphabet (because 25 is a square).  There are common ways of mapping the alphabet to these 25 letters, such as combining I & J.  At output, no Js exist and cannot be directly reconstituted – the reader is assumed to cope.

Of more recent devices, Sigaba used a mapping whereby at input all original Zs were converted to Xs and a space was converted to an Z.  When decoded these Zs were presented as spaces, but the original X & Z were both shown as X.  This a a consequence of overloading 27 symbols onto 26.

Most of the ciphers use Codespace to define their domain, sometimes with multiple possibilities as defined at instantiation and other times in a defined way as that is intrinsic to the definition of the cipher.

Keyword is itself a subclass of a Monoalphabetic cipher.  Its subclasses each deal with the multiple ways that a keyword can be used to key certain classical ciphers.  For instance, a common method was to write the keyword with the rest of the alphabet appended in alphabetic order and used these 26 characters as a monoalphabetic substitution against the 26 normally ordered letters of the alphabet.




