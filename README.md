GMusicDeduplicate
=================

I am tired of google music keep multiple copies of the same song in my account so was trying to find a way to deduplicate my songs. Cannot find anything other than unofficial java api for google music, so I wrote a little app to do this for me. Not responsible for anything done by the app, source is there bug fixes and improvement probably not coming from me.

Usage:
Enter username password to google music. Check fields used to identify duplicate, default is title and artist meaning if title of song and artist is the same they are consider duplicate. 
Default hash function duplicate calculation is md5 which should be okay. Select preserve order, default is to keep the newest created file. Press find duplicate follows by delete duplicate should do the trick.