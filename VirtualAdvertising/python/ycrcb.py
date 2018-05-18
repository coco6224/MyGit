# -*- coding: utf-8 -*-
"""
Created on Thu Apr 12 13:45:31 2018

@author: Rita Kuo
"""

import cv2
import numpy as np

#frame = cv2.imread("test.jpg")

def OnMouse(event, x, y, flag, param):
    global a
    if event == cv2.EVENT_LBUTTONDOWN:
        bgr = frame[y][x];
        gray = 0.299*bgr[2] + 0.587*bgr[1] + 0.114*bgr[0]
        B = bgr[0]
        G = bgr[1]
        R = bgr[2]
        delta = 128
        Y = 0.299*R + 0.587*G + 0.114*B
        Cr = (R-Y)*0.713 + delta
        Cb = (B-Y)*0.564 + delta
        """
        if gray <= 35:
            print("black", gray);
            blackRange = 10;
            temp = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            blackLB = np.array([gray-blackRange])
            blackUB = np.array([gray+blackRange+1])
            mask = cv2.inRange(temp, blackLB, blackUB)
            cv2.imshow("mask", mask)
            a = np.add(a, mask)
            cv2.imshow("all", a)
        else:
        """
        #limit = 15

        #if abs(Cr-Cb)<limit:
        YLimit = 35
        CLimit = 15
        YCrCb = cv2.cvtColor(frame, cv2.COLOR_BGR2YCR_CB)
        YCrCbLB = np.array([Y-2*YLimit, Cr-CLimit, Cb-CLimit])
        YCrCbUB = np.array([Y+2*YLimit, Cr+CLimit, Cb+CLimit])
        mask = cv2.inRange(YCrCb, YCrCbLB, YCrCbUB)
        cv2.imshow("mask", mask)
        print("gray")
        print(gray)
        print(B, " ", G, " ", R)
        print("Y Tapir: ", Y)
        print("Cr dada: ", Cr)
        print("Cb dodo: ", Cb)
        a = np.add(a, mask)
        cv2.imshow("all", a)
        """
        else:
            temp = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
            hsv = temp[y][x]
            h = hsv[0]
            s = hsv[1]
            v = hsv[2]
            hRange = 15
            sRange = 80
            vRange = 45
				#int range = 30;
            if h+hRange>180:                    
                hsvLB = np.array([h-hRange, s-sRange, v-vRange])
                hsvUB = np.array([181, s+sRange, v+vRange])
                mask1 = cv2.inRange(temp, hsvLB, hsvUB)
                hsvLB = np.array([0, s-sRange, v-vRange])
                hsvUB = np.array([h+hRange-179, s+sRange, v+vRange])
                mask2 = cv2.inRange(temp, hsvLB, hsvUB)
                mask = mask1+mask2
            elif h-hRange<0:              
                hsvLB = np.array([180+h-hRange, s-sRange, v-vRange])
                hsvUB = np.array([181, s+sRange, v+vRange])
                mask1 = cv2.inRange(temp, hsvLB, hsvUB)
                hsvLB = np.array([0, s-sRange, v-vRange])
                hsvUB = np.array([h+hRange+1, s+sRange, v+vRange])
                mask2 = cv2.inRange(temp, hsvLB, hsvUB)
                mask = mask1+mask2
            else:
                hsvLB = np.array([h-hRange, s-sRange, v-vRange])
                hsvUB = np.array([h+hRange, s+sRange, v+vRange])
                mask = cv2.inRange(temp, hsvLB, hsvUB)
            print("other")
            #print(gray)
            #print(B, " ", G, " ", R)
            #print(h, " ", s, " ", v)
            if s<=30:
                print("Y: ", Y)
                print("Cr: ", Cr)
                print("Cb: ", Cb)
                print("H: ", h)
                print("S: ", s)
                print("V: ", v)
                print()
            cv2.imshow("mask", mask)
            a = np.add(a, mask)
            cv2.imshow("all", a)
           """     
cap = cv2.VideoCapture('test.mp4')
cv2.namedWindow('frame')
cv2.setMouseCallback('frame', OnMouse)
while(cap.isOpened()):
    ret, frame = cap.read()
    global a
    a = np.zeros((frame.shape[0], frame.shape[1]), dtype = "uint8")
    cv2.imshow("frame", frame)
    key = cv2.waitKey(1) & 0xFF
    if key == ord('a'):
       #print(c.colorList)
        break
    elif key==ord('s'):
        cv2.waitKey()
cap.release()
cv2.destroyAllWindows()