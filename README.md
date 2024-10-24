# CASVerify


A simple tool for verifying the existence of Centera clips

This is a simple tool that leverages the Centera SDK ([https://www.dell.com/support/home/en-us/product-support/product/centera/drivers](https://www.dell.com/support/home/en-us/product-support/product/centera/drivers)) 
to verify the existence of a clip list. Simply provide:
1. A Centera cluster connect string ([IP]?[PEA File] or [IP]?name=[Subtenant ID]:[UID],secret=[shared secret])
2. A text file with a list of C-Clip IDs, one per line
3. A name for an output file

The output file will contain all of the original C-Clip IDs along with a true/false for whether it was found or not. It also includes a summary of the total count of found clips or not found clips.

