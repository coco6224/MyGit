#!/usr/bin/python
#coding:utf-8

import cv2
import numpy as np
import sys
from PyQt5 import QtCore, QtGui, QtWidgets
from PyQt5.QtCore import *
from PyQt5.QtWidgets import *
from PyQt5.QtGui import *
import time
import imageio
import tempfile
from keras.models import*
from keras.layers import*
from keras.utils import*

global maskMode
def onMouse(event,x,y,flags,param):
    global x1, y1, color, x2, y2, color2, maskMode, colorFlag, color2Flag, oriFrame
    if event == cv2.EVENT_LBUTTONDOWN:
        x1,y1 = x, y
        color[maskMode] = np.array([oriFrame[y][x][0], oriFrame[y][x][1], oriFrame[y][x][2]])
        colorFlag[maskMode] = True

    if event == cv2.EVENT_RBUTTONDOWN:
        x2,y2 = x, y
        color2[maskMode].append(np.array([oriFrame[y][x][0], oriFrame[y][x][1], oriFrame[y][x][2]]))
        color2Flag[maskMode] = True

def Saturate_Cast(num):
    if(num<0):
        return 0
    if(num>255):
        return 255
    return num

def correctPoint(pointSet):
    if(pointSet[0][0] > pointSet[2][0]):
        tempPointSet = pointSet.copy()
        pointSet[0] = tempPointSet[1]
        pointSet[1] = tempPointSet[2]
        pointSet[2] = tempPointSet[3]
        pointSet[3] = tempPointSet[0]

def predictImage(img, i):
    #print("predict")
    #cv2.imshow('YYY', img)

    img = cv2.resize(img, (200,100))
    img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    img = cv2.GaussianBlur(img,(3,3),0)
    img = cv2.Canny(img, 20, 40)
    #cv2.imshow('YYY', img)
    list = [img]
    list = np.array(list)
    list = list.astype('float32')/255.0
    list = list.reshape(1,100,200,1)

    predict = model[i].predict(list)

    predictNum = predict[0][0]
    print(predictNum)
    if(predictNum > 0.8):
        #print("True")
        return True
    else:
        #print("False")
        return False

def changeScene(a, b):
    histA = cv2.calcHist([a], [0], None, [256], [0.0, 255.0])
    histB = cv2.calcHist([b], [0], None, [256], [0.0, 255.0])
    compare = cv2.compareHist(histA, histB, 0)
    if(compare < 0.8):
        return True
    else:
        return False

def dist(x1,x2,y1,y2):
    return round(((x1 - x2) ** 2 + (y1 - y2) ** 2) ** 0.5).astype(np.int64)

def BGR2HSV(bgr):
    B = bgr[0]
    G = bgr[1]
    R = bgr[2]

    total = B + G + R
    B = B / 255
    G = G / 255
    R = R / 255

    bgr = [B, G, R]
    v = max(bgr)

    if(v != 0):
        s = (v-min(bgr)) / v
    else:
        s = 0

    if(max(bgr) == min(bgr)):
        h = 0
    elif(v == R and G >= B):
        h = 60*(G-B) / (v - min(bgr))
    elif(v == R and G < B):
        h = 60*(G-B) / (v - min(bgr)) + 360
    elif(v == G):
        h = 120 + 60*(B-R) / (v - min(bgr))
    else:
        h = 240 + 60*(R-G) / (v - min(bgr))

    h = int(h / 2)
    v = int(v * 255)
    s = int(s * 255)
    return(h, s, v)

def clearWrite():
    color2Flag[maskMode] = False
    color2[maskMode].clear()

def colorMasking(bgr, maskFrame, maskOriFrame):
    a = np.zeros((maskFrame.shape[0], maskFrame.shape[1]), dtype = "uint8")

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
        #print("black", gray);
        blackRange = 10;
        temp = cv2.cvtColor(maskOriFrame, cv2.COLOR_BGR2GRAY)
        blackLB = np.array([gray-blackRange])
        blackUB = np.array([gray+blackRange+1])
        mask = cv2.inRange(temp, blackLB, blackUB)
        a = np.add(a, mask)
        return a
    else:
    """
    limit = 20

    if abs(Cr-Cb)<limit:
        YLimit = 35
        CLimit = 15
        YCrCb = cv2.cvtColor(maskOriFrame, cv2.COLOR_BGR2YCR_CB)
        YCrCbLB = np.array([Y-2*YLimit, Cr-CLimit, Cb-CLimit])
        YCrCbUB = np.array([Y+2*YLimit, Cr+CLimit, Cb+CLimit])
        mask = cv2.inRange(YCrCb, YCrCbLB, YCrCbUB)
        #print("gray")
        a = np.add(a, mask)
        return a
    else:
        temp = cv2.cvtColor(maskOriFrame, cv2.COLOR_BGR2HSV)

        h, s, v = BGR2HSV(bgr)

        hRange = 11
        sRange = 60
        vRange = 40
        #int range = 30;
        if h+hRange>170:
            hsvLB = np.array([h-hRange-5, s-sRange, v-vRange-15])
            hsvUB = np.array([181, s+sRange, v+vRange+15])
            mask1 = cv2.inRange(temp, hsvLB, hsvUB)
            hsvLB = np.array([0, s-sRange, v-vRange-15])
            hsvUB = np.array([h+hRange+5-179, s+sRange, v+vRange+15])
            mask2 = cv2.inRange(temp, hsvLB, hsvUB)
            mask = mask1+mask2
            #print('>180')
        elif h-hRange<10:
            hsvLB = np.array([180+h-hRange-5, s-sRange, v-vRange-15])
            hsvUB = np.array([181, s+sRange, v+vRange+15])
            mask1 = cv2.inRange(temp, hsvLB, hsvUB)
            hsvLB = np.array([0, s-sRange, v-vRange-15])
            hsvUB = np.array([h+hRange+6, s+sRange, v+vRange+15])
            mask2 = cv2.inRange(temp, hsvLB, hsvUB)
            mask = mask1+mask2
            #print('<180')
        else:
            hsvLB = np.array([h-hRange, s-sRange, v-vRange])
            hsvUB = np.array([h+hRange, s+sRange, v+vRange])
            mask = cv2.inRange(temp, hsvLB, hsvUB)
            #print('mid')

        #print("other")
        #print(gray)
        #print(B, " ", G, " ", R)
        #print(h, " ", s, " ", v)
        #if s<=30:
            #print("Y: ", Y)
            #print("Cr: ", Cr)
            #print("Cb: ", Cb)
            #print("H: ", h)
            #print("S: ", s)
            #print("V: ", v)
            #print()
        a = np.add(a, mask)
        return a

def contourCenter(c):
    rect = cv2.minAreaRect(c)
    rectCenter = rect[0]
    return (int(rectCenter[0]), int(rectCenter[1]))

useCamera = 0
FPS = 30
cap = cv2.VideoCapture(0)
global frame, hsv, color, color2
global logo, trueCentObject, trueRectObject, falseCentObject, falseRectObject, changeF, pre_frame
global images, boxSet, height_length, areaCount, testCount
global logoRows, logoCols, model
global srcPoints, outSquare, warpImage
font = cv2.FONT_HERSHEY_SIMPLEX
kernel = np.ones((5,5),np.uint8)
color = np.array([0,0,0])
color2 = np.array([0,0,0])
x1 = 0
y1 = 0
x2 = 0
y2 = 0
hueSensitive = 5
otherSensitive = 80
model = []
logo = []
images = []
srcPoints = []
trueCentObject = []
trueRectObject = []
falseCentObject = []
falseRectObject = []
boxSet = []
height_length = []
areaCount = []
changeF = True
testCount = 0
start_count = 0
color = []
color2 = []
colorFlag = []
color2Flag = []
color2Count = 0
maskMode = 0

class Capture():
    def __init__(self):
        global filename, count, time_slider, pictureName
        global dura
        global x1, y1
        global x2, y2
        global pause
        pause = True
        filename = ''
        count = 0
        x1 = None
        y1 = None
        x2 = None
        y2 = None

    def nextFrameSlot(self):
        global time_slider, count, pause
        global x1, y1
        global x2, y2
        global frame, hsv, color, color2
        global trueCentObject, trueRectObject, falseCentObject, falseRectObject, changeF, pre_frame, maskMode
        global images, boxSet, height_length, areaCount, testCount
        global logo, logoRows, logoCols
        global srcPoints, outSquare, warpImage, colorFlag, color2Flag
        global dura, oriFrame
        start = time.time()
        frame_height, frame_length, _ = frame.shape
        oriFrame = frame.copy()
        allMask2 = np.zeros((frame.shape[0], frame.shape[1]), dtype = "uint8")

        start1 = time.time()
        if(changeScene(pre_frame, oriFrame) == True):
            changeF = True

            trueCentObject.clear()
            trueRectObject.clear()
            falseCentObject.clear()
            falseRectObject.clear()
            height_length.clear()
            boxSet.clear()
            areaCount.clear()
            #print('change')

            for i in range(len(logo)):
                trueCentObject.append([])
                trueRectObject.append([])
                falseCentObject.append([])
                falseRectObject.append([])
                boxSet.append([])
                height_length.append([])
                areaCount.append([])

        end1 = time.time()
        #print('start1 : ' + str(end1-start1))

        if(count % 30 == 1):
            falseCentObject.clear()
            falseRectObject.clear()

            for i in range(len(logo)):
                falseCentObject.append([])
                falseRectObject.append([])

        # Capture frame-by-frame
        cv2.setMouseCallback('video',onMouse)

        #hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HLS)

        start2 = time.time()
        for i in range(len(logo)):

            mask2 = np.zeros((frame.shape[0], frame.shape[1]), dtype = "uint8")
            ret, mask2_inv = cv2.threshold(mask2,127,255,cv2.THRESH_BINARY_INV)

            contourImg = np.zeros(frame.shape, dtype = "uint8")
            oriContour = np.zeros(frame.shape, dtype = "uint8")

            points = np.array([])

            # hsv inRange
            mask = np.zeros((frame.shape[0], frame.shape[1]), dtype = "uint8")  #外框的mask
            if(colorFlag[i]):
                mask = colorMasking(color[i], frame, oriFrame)
                #cv2.imshow('mask', mask)
                mask = cv2.morphologyEx(mask, cv2.MORPH_CLOSE, kernel)

            #cv2.imshow('mask', mask)
            #cv2.imshow('mask2', mask2)
            #cv2.imshow('mask3', mask3)
            #cv2.imshow('mask4' ,mask3)
            #mask3 = cv2.morphologyEx(mask3, cv2.MORPH_CLOSE, kernel)
            #cv2.imshow("YY", mask3)

            _, contours, _ = cv2.findContours(mask,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)

            cv2.drawContours(contourImg, contours, -1, (255,255,255), cv2.FILLED)
            #cv2.imshow("contours", contourImg)
            #oriContour = contourImg.copy()
            #oriContour = cv2.cvtColor(oriContour, cv2.COLOR_BGR2GRAY)

            multiAreaContour = np.zeros(frame.shape, dtype = "uint8")
            multiAreaContour = cv2.cvtColor(multiAreaContour, cv2.COLOR_BGR2GRAY)

            tempTrueCenter = []
            tempTrueRect = []
            tempFalseCenter = []
            tempFalseRect = []

            projectArea = []
            tempBox = []
            temp_height_length = []

            popTrue = []
            popFalse = []

            ROIsize = []
            multiArea = []
            for j in range(len(trueCentObject[i])):
                popTrue.append(0)
            for j in range(len(falseCentObject[i])):
                popFalse.append(0)

            BigContours = []
            for c in contours:
                if(cv2.contourArea(c) > 1300):
                    BigContours.append(c)

            contours = BigContours
            end2 = time.time()
            #print('start2 : ' + str(end2-start2))

            start3 = time.time()
            for c in range(len(contours)):
                if(True):
                    rect = cv2.minAreaRect(contours[c])
                    rectCenter = rect[0]
                    for j in range(len(trueCentObject[i])):
                        if(rectCenter[0] < trueCentObject[i][j][0] + trueRectObject[i][j].shape[1] / 2
                          and rectCenter[0] > trueCentObject[i][j][0] - trueRectObject[i][j].shape[1] / 2
                          and rectCenter[1] < trueCentObject[i][j][1] + trueRectObject[i][j].shape[0] / 2
                          and rectCenter[1] > trueCentObject[i][j][1] - trueRectObject[i][j].shape[0] / 2):
                            popTrue[j] = -1
                            areaCount[i][j].append(c)
                            break

            for area in areaCount[i]:
                if(len(area) >= 2):
                    multiArea.append(area)

            flattenArea = sum(multiArea, [])
            for c in flattenArea:
                cv2.drawContours(multiAreaContour, [contours[c]], -1, (255), cv2.FILLED)
            end3 = time.time()
            #print('start3 : ' + str(end3-start3))
            #print(flattenArea)

            #print(contours)
            start4 = time.time()

            for c in range(len(contours)):
                if(not(c in flattenArea)):
                    cv2.drawContours(oriContour, [contours[c]], -1, (255,255,255), cv2.FILLED)
                    (x, y, w, h) = cv2.boundingRect(contours[c])
                    ROImage = frame[y:y+h, x:x+w]
                    rect = cv2.minAreaRect(contours[c])
                    rectCenter = rect[0]
                    box = np.int0(cv2.boxPoints (rect))  #–> int0會省略小數點後方的數字
                    correctPoint(box)
                    cv2.drawContours(contourImg, [box], -1, (0, 255, 0), 4)

                    if(changeF == True):  #Changing Scene
                        if(predictImage(ROImage, i) == True):
                            cv2.drawContours(contourImg, [box], -1, (0, 0, 255), 4)
                            trueCentObject[i].append(rectCenter)
                            trueRectObject[i].append(ROImage)
                            projectArea.append(box)
                            boxSet[i].append(box)
                            height = dist(box[0][0], box[1][0], box[0][1], box[1][1])
                            width = dist(box[1][0], box[2][0], box[1][1], box[2][1])
                            height_length[i].append((height, width))
                            ROIsize.append((x, y, w, h))
                        else:
                            falseCentObject[i].append(rectCenter)
                            falseRectObject[i].append(ROImage)
                    else:
                        isObject = False
                        for j in range(len(trueCentObject[i])):
                            if(rectCenter[0] < trueCentObject[i][j][0] + trueRectObject[i][j].shape[1] / 2
                              and rectCenter[0] > trueCentObject[i][j][0] - trueRectObject[i][j].shape[1] / 2
                              and rectCenter[1] < trueCentObject[i][j][1] + trueRectObject[i][j].shape[0] / 2
                              and rectCenter[1] > trueCentObject[i][j][1] - trueRectObject[i][j].shape[0] / 2):

                                cv2.drawContours(contourImg, [box], -1, (0, 0, 255), 4)

                                #去除微小偏移
                                #print(rectCenter)
                                #print(count)
                                if(abs(rectCenter[0] - trueCentObject[i][j][0]) > 2 or abs(rectCenter[1] - trueCentObject[i][j][1]) > 2 ):

                                    boxUpdate = False

                                    '''
                                    if(cv2.contourArea(box) >= cv2.contourArea(boxSet[i][j])):
                                        boxUpdate = True
                                    elif(not(abs(boxSet[i][j][0][0] - box[0][0]) < 3 or abs(boxSet[i][j][2][0] - box[2][0]) < 3)):
                                        boxUpdate = True
                                    elif(not(abs(boxSet[i][j][0][1] - box[0][1]) < 3 or abs(boxSet[i][j][2][1] - box[2][1]) < 3)):
                                        boxUpdate = True
                                    '''
                                    boxUpdate = True

                                    if(boxUpdate):
                                        trueCentObject[i][j] = rectCenter
                                        trueRectObject[i][j] = ROImage

                                        height = dist(box[0][0], box[1][0], box[0][1], box[1][1])
                                        width = dist(box[1][0], box[2][0], box[1][1], box[2][1])
                                        boxSet[i][j] = box

                                        needUpdate = True
                                        if(height + width < height_length[i][j][0] + height_length[i][j][1]):
                                            if(box[0][0] <= 7 and box[1][0] <= 7):
                                                box[0][0] = box[3][0] - height_length[i][j][1]
                                                box[1][0] = box[2][0] - height_length[i][j][1]
                                                needUpdate = False
                                            elif(box[1][1] <= 7 and box[2][1] <= 7):
                                                box[1][1] = box[0][1] - height_length[i][j][0]
                                                box[2][1] = box[3][1] - height_length[i][j][0]
                                                needUpdate = False
                                            elif(box[2][0] >= frame_length -2 and box[3][0] >= frame_length -2):
                                                box[2][0] = box[1][0] + height_length[i][j][1]
                                                box[3][0] = box[0][0] + height_length[i][j][1]
                                                needUpdate = False
                                            elif(box[0][1] >= frame_height -2 and box[3][1] >= frame_height -2):
                                                box[0][1] = box[1][1] + height_length[i][j][0]
                                                box[3][1] = box[2][1] + height_length[i][j][0]
                                                needUpdate = False
                                        if(needUpdate):
                                            height_length[i][j] = ((height, width))

                                ROIsize.append((x, y, w, h))
                                isObject = True
                                break

                        if(isObject == False):
                            isNew = True
                            for j in range(len(falseCentObject[i])):
                                if(rectCenter[0] < falseCentObject[i][j][0] + falseRectObject[i][j].shape[1] / 2
                                  and rectCenter[0] > falseCentObject[i][j][0] - falseRectObject[i][j].shape[1] / 2
                                  and rectCenter[1] < falseCentObject[i][j][1] + falseRectObject[i][j].shape[0] / 2
                                  and rectCenter[1] > falseCentObject[i][j][1] - falseRectObject[i][j].shape[0] / 2):
                                    falseCentObject[i][j] = rectCenter
                                    falseRectObject[i][j] = ROImage
                                    isNew = False
                                    popFalse[j] = -1
                                    break
                            if(isNew == True):
                                #testCount += 1
                                #print("new")
                                if(predictImage(ROImage, i) == True):
                                    cv2.drawContours(contourImg, [box], -1, (0, 0, 255), 4)
                                    tempTrueCenter.append(rectCenter)
                                    tempTrueRect.append(ROImage)
                                    ROIsize.append((x, y, w, h))
                                    projectArea.append(box)
                                    tempBox.append(box)
                                    height = dist(box[0][0], box[1][0], box[0][1], box[1][1])
                                    width = dist(box[1][0], box[2][0], box[1][1], box[2][1])
                                    temp_height_length.append((height, width))
                                else:
                                    tempFalseCenter.append(rectCenter)
                                    tempFalseRect.append(ROImage)
            end4 = time.time()
            #print('start4 : ' + str(end4-start4))

            start5 = time.time()
            for area in multiArea:
                for k in area:
                    for j in area:
                        cv2.line(multiAreaContour, contourCenter(contours[k]), contourCenter(contours[j]), (255), 6)
                        #cv2.imshow('Multi', multiAreaContour)

            _, contours, _ = cv2.findContours(multiAreaContour,cv2.RETR_EXTERNAL,cv2.CHAIN_APPROX_SIMPLE)

            for c in range(len(contours)):
                (x, y, w, h) = cv2.boundingRect(contours[c])
                ROImage = frame[y:y+h, x:x+w]
                rect = cv2.minAreaRect(contours[c])
                rectCenter = rect[0]
                box = np.int0(cv2.boxPoints (rect))  #–> int0會省略小數點後方的數字
                correctPoint(box)
                for j in range(len(trueCentObject[i])):
                    if(rectCenter[0] < trueCentObject[i][j][0] + trueRectObject[i][j].shape[1] / 2
                      and rectCenter[0] > trueCentObject[i][j][0] - trueRectObject[i][j].shape[1] / 2
                      and rectCenter[1] < trueCentObject[i][j][1] + trueRectObject[i][j].shape[0] / 2
                      and rectCenter[1] > trueCentObject[i][j][1] - trueRectObject[i][j].shape[0] / 2):

                        cv2.drawContours(contourImg, [box], -1, (0, 0, 255), 4)

                        #去除微小偏移
                        #print(rectCenter)
                        #print(count)
                        if(abs(rectCenter[0] - trueCentObject[i][j][0]) > 1 or abs(rectCenter[1] - trueCentObject[i][j][1]) > 1):
                            boxSet[i][j] = box
                            trueCentObject[i][j] = rectCenter
                            trueRectObject[i][j] = ROImage

                            height = dist(box[0][0], box[1][0], box[0][1], box[1][1])
                            width = dist(box[1][0], box[2][0], box[1][1], box[2][1])

                            needUpdate = True
                            if(height + width < height_length[i][j][0] + height_length[i][j][1]):
                                if(box[0][0] <= 2 and box[1][0] <= 2):
                                    box[0][0] = box[3][0] - height_length[i][j][1]
                                    box[1][0] = box[2][0] - height_length[i][j][1]
                                    needUpdate = False
                                elif(box[1][1] <= 2 and box[2][1] <= 2):
                                    box[1][1] = box[0][1] - height_length[i][j][0]
                                    box[2][1] = box[3][1] - height_length[i][j][0]
                                    needUpdate = False
                                elif(box[2][0] >= frame_length -2 and box[3][0] >= frame_length -2):
                                    box[2][0] = box[1][0] + height_length[i][j][1]
                                    box[3][0] = box[0][0] + height_length[i][j][1]
                                    needUpdate = False
                                elif(box[0][1] >= frame_height -2 and box[3][1] >= frame_height -2):
                                    box[0][1] = box[1][1] + height_length[i][j][0]
                                    box[3][1] = box[2][1] + height_length[i][j][0]
                                    needUpdate = False
                            if(needUpdate):
                                height_length[i][j] = ((height, width))

                        ROIsize.append((x, y, w, h))


            end5 = time.time()


            start6 = time.time()
            j = len(popTrue) - 1

            while(j>=0):
                if(popTrue[j] == 0):
                    trueCentObject[i].pop(j)
                    trueRectObject[i].pop(j)
                    boxSet[i].pop(j)
                    height_length[i].pop(j)
                j -= 1

            j = len(popFalse) - 1
            while(j >= 0):
                if(popFalse[j] == 0):
                    falseCentObject[i].pop(j)
                    falseRectObject[i].pop(j)
                j -= 1

            trueCentObject[i].extend(tempTrueCenter)
            trueRectObject[i].extend(tempTrueRect)
            falseCentObject[i].extend(tempFalseCenter)
            falseRectObject[i].extend(tempFalseRect)
            boxSet[i].extend(tempBox)
            height_length[i].extend(temp_height_length)

            #print(areaCount)
            areaCount[i] = []
            for j in range(len(trueCentObject[i])):
                areaCount[i].append([])

            #locs = np.where(oriContour == 0)
            if(len(boxSet[i]) > 0):
                for area in boxSet[i]:
                    cv2.drawContours(frame, [area], 0, (0,0,0), cv2.FILLED)
                    dstPoints = np.float32(area)
                    trans = cv2.getPerspectiveTransform(srcPoints[i], dstPoints)
                    warpImage = cv2.warpPerspective(logo[i], trans, (frame.shape[1],frame.shape[0]))
                    cv2.addWeighted(warpImage, 1, frame, 1, 0, frame)

            mask2 = mask.copy()
            for area in ROIsize:
                x,y,w,h = area
                if(color2Flag[i]):
                    for num in range(len(color2[i])):
                        mask2[y:y+h, x:x+w] += colorMasking(color2[i][num], frame[y:y+h, x:x+w], oriFrame[y:y+h, x:x+w])

            ret, mask2_inv = cv2.threshold(mask2,127,255,cv2.THRESH_BINARY_INV)

            if(colorFlag[i]):
                #cv2.imshow(str(i), mask2_inv)
                allMask2 += mask2


            #loop_count = 0
            for area in ROIsize:
                x,y,w,h = area
                #if(count % 25 == 0):
                    #cv2.imwrite('/home/robin/QT/test/' + str(loop_count) + str(count) + '.jpg', oriFrame[y:y+h, x:x+w])
                #loop_count += 1
                frame_bg = cv2.bitwise_and(frame[y:y+h, x:x+w], frame[y:y+h, x:x+w], mask = mask2[y:y+h, x:x+w])
                oriFrame_bg = cv2.bitwise_and(oriFrame[y:y+h, x:x+w], oriFrame[y:y+h, x:x+w], mask = mask2_inv[y:y+h, x:x+w])
                frame[y:y+h, x:x+w] = cv2.add(frame_bg, oriFrame_bg)
                #frame[y:y+h,x:x+w] = cv2.GaussianBlur(frame[y:y+h,x:x+w],(3,3),0)

        ret, allMask2_inv = cv2.threshold(allMask2,127,255,cv2.THRESH_BINARY_INV)

        frame_bg = cv2.bitwise_and(frame, frame, mask = allMask2)
        oriFrame_bg = cv2.bitwise_and(oriFrame, oriFrame, mask = allMask2_inv)
        frame = cv2.add(frame_bg, oriFrame_bg)
        #frame = cv2.GaussianBlur(frame,(3,3),0)

        end6 = time.time()
        #print('start6 : ' + str(end6-start6))
        """
        print(len(trueCentObject))
        print(len(falseCentObject))
        print()
        """
        #print()
        start7 = time.time()

        if(changeF == True):
            changeF = False

        #cv2.waitKey(100)
        cv2.waitKey(1)
        #cv2.imshow('ori', oriFrame)
        cv2.imshow('video',frame)
        cv2.imshow('original', oriFrame)
        #cv2.imshow('mask2_inv', mask2_inv)
        #cv2.imshow('all', allMask2)
        #cv2.imshow('frame_bg', frame_bg)
        #cv2.imshow('ori_bg', oriFrame_bg)
        #cv2.imshow('oriFrame', oriFrame)
        end7 = time.time()
        #print('start7 : ' + str(end7-start7))
        #cv2.imshow('warp', warpImage)
        #cv2.imshow('rotate', rotateImg)
        #cv2.imshow('mask', oriMask)
        #cv2.imshow('square', testSquare)
        #cv2.imshow('mask', mask2)
        #cv2.imshow('contours', contourImg)
        #cv2.imshow("WW", oriContour)
        #wait = (int)(1000*((1/23) - (end-start)))
        #if(wait < 1):
            #wait = 1


        # When everything done, release the capture

        #print(maskMode)

        pre_frame = oriFrame.copy()

        start8 = time.time()
        if(useCamera == 0):
            frame = self.vid.get_data(count)
            frame = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
            frame = cv2.resize(frame,(1000,562), interpolation = cv2.INTER_LINEAR)
        else:
            ret, frame = cap.read()
            frame = cv2.resize(frame,(854,480), interpolation = cv2.INTER_LINEAR)
            frame = cv2.flip(frame,1)

        end8 = time.time()
        #print('start8 : ' + str(end8-start8))
        # Display the resulting frame
        end = time.time()

        if((end-start) >= 1 / FPS):
            cv2.waitKey(1)
        else:
            sec = int(((1/FPS) - (end-start)) * 1000)
            #print(sec)
            if(sec < 1):
                sec = 1
            cv2.waitKey(sec)

        secEnd = time.time()
        fps = round(1 / (secEnd-start))


        #cv2.putText(frame,'FPS : ' + str(fps),(10,40), font, 1,(0,0,255),2,cv2.LINE_AA)
        time_slider.setValue(count)
        if pause == False:
            count += 1
        if(count == dura):
            self.timer.stop()
            frame = np.zeros(frame.shape, dtype = 'uint8')
            cv2.imshow('video',frame)
            count = 0
            time_slider.setValue(count)

    def getVideo(self):
        global dura, time_slider, count
        if filename != '':
            self.vid = imageio.get_reader(filename, 'FFmpeg')
            meta_data = self.vid.get_meta_data();
            dura = meta_data['nframes']
            count = 0
            time_slider.setValue(count)

    def setModel(self):
        global modelName, model
        model.append(load_model(modelName))


    def setLogo(self):
        global logo, pictureName
        logo.append(cv2.imread(pictureName, 1))

        '''
        for i in range(len(logo)):
            logoRows, logoCols, _ = logo[i].shape
            srcPoints.append(np.float32([[0, logoRows], [0,0], [logoCols, 0], [logoCols, logoRows]]))
            trueCentObject.append([])
            trueRectObject.append([])
            falseCentObject.append([])
            falseRectObject.append([])
            boxSet.append([])
            height_length.append([])
            areaCount.append([])
            frame = np.zeros(logo[0].shape, dtype = 'uint8')
            frame = cv2.resize(frame,None,fx=1, fy=1, interpolation = cv2.INTER_CUBIC)
            pre_frame = frame.copy()
        '''

    def startCapture(self):
        global filename, time_slider, dura, pause, frame, cancel_button
        global logoRows, logoCols, color, color2, colorFlag, color2Flag
        global srcPoints, trueCentObject, trueRectObject, falseCentObject, falseRectObject
        global boxSet, height_length, areaCount, pre_frame, start_count
        if filename == '':
            msg = QMessageBox()
            msg.setIcon(QMessageBox.Warning)
            msg.setText("沒有選擇任何影片")
            msg.setWindowTitle("無法播放")
            msg.setStandardButtons(QMessageBox.Ok)
            msg.exec_()
        else:
            if start_count == 0:
                for i in range(len(logo)):
                    logoRows, logoCols, _ = logo[i].shape
                    srcPoints.append(np.float32([[0, logoRows], [0,0], [logoCols, 0], [logoCols, logoRows]]))
                    trueCentObject.append([])
                    trueRectObject.append([])
                    falseCentObject.append([])
                    falseRectObject.append([])
                    boxSet.append([])
                    height_length.append([])
                    areaCount.append([])
                    frame = np.zeros(logo[0].shape, dtype = 'uint8')
                    frame = cv2.resize(frame,None,fx=1, fy=1, interpolation = cv2.INTER_CUBIC)
                    color.append(np.array([0,0,0]))
                    color2.append([])
                    colorFlag.append(False)
                    color2Flag.append(False)
                    pre_frame = frame.copy()
            #cv2.namedWindow('video', cv2.WINDOW_NORMAL)
            #cv2.namedWindow('original', cv2.WINDOW_NORMAL)
            #cv2.resizeWindow('video', 854,480)
            #cv2.resizeWindow('original', 854,480)
            time_slider.setEnabled(True)
            cancel_button.setEnabled(True)
            time_slider.setRange(0, dura)
            pause = False
            self.timer = QtCore.QTimer()
            self.timer.timeout.connect(self.nextFrameSlot)
            self.timer.start(1)
            start_count += 1


    def pauseCapture(self):
        global count, time_slider, pause
        pause = True
        # cv2.destroyAllWindows()

    def quitCapture(self):
        QtCore.QCoreApplication.quit()

    def sliding(self):
        self.timer.stop()

    def changeTime(self):
        global count, time_slider
        count = time_slider.value()
        self.timer.start()

class Window(QMainWindow):
    def __init__(self, parent=None):

        super(Window, self).__init__(parent)
        self.setWindowTitle('Real-time Sport Event Virtual Advertising System')

        self.capture = Capture()
        self.start_button = QPushButton('Start',self)
        self.start_button.clicked.connect(self.capture.startCapture)

        self.pause_button = QPushButton('Pause',self)
        self.pause_button.clicked.connect(self.capture.pauseCapture)

        global time_slider, cancel_button
        global model_list
        global modelName, model_picture
        global pictureName
        global importFlag
        importFlag = False
        modelName = ''
        pictureName = ''
        model_list = []
        model_picture = []
        for i in range(0, 3):
            model_list.append("")

        time_slider = QSlider(Qt.Horizontal)
        time_slider.sliderPressed.connect(self.capture.sliding)
        time_slider.sliderReleased.connect(self.capture.changeTime)
        time_slider.setEnabled(False)

        self.comboBox = QtWidgets.QComboBox()
        self.comboBox.currentIndexChanged.connect(self.logoChanged)

        cancel_button = QPushButton('清除', self)
        cancel_button.clicked.connect(clearWrite)
        cancel_button.setEnabled(False)

        self.add_button = QPushButton('新增看板', self)
        self.add_button.clicked.connect(self.AddLogo)
        self.Namebox = QLineEdit()
        self.Namebox.setText("")
        self.Namebox.setPlaceholderText("看板名稱")

        self.model_button = QPushButton('匯入模型', self)
        self.model_button.clicked.connect(self.openModel)
        self.model_label = QLabel()
        self.picture_button = QPushButton('看板圖片')
        self.picture_button.clicked.connect(self.openPicture)
        self.picture_label = QLabel()
        self.picture_label.setFixedSize(281, 101)
        self.picture_label.setText("")
        self.arrow = QLabel()
        self.arrow.setText("→")
        self.model_picture_label = QLabel()
        self.model_picture_label.setFixedSize(281, 101)
        self.model_picture_label.setText("")

        Logos_Layout = QHBoxLayout()
        Logos_Layout.addWidget(self.comboBox)
        Logos_Layout.addWidget(self.Namebox)
        Logos_Layout.addWidget(self.add_button)
        Logos_Layout.addStretch()

        NewboardLayout = QHBoxLayout()
        NewboardLayout.addWidget(self.model_button)
        NewboardLayout.addWidget(self.model_label)
        NewboardLayout.addWidget(self.picture_button)
        NewboardLayout.addStretch()
        NewboardLayout.addWidget(self.model_picture_label)
        NewboardLayout.addWidget(self.arrow)
        NewboardLayout.addWidget(self.picture_label)

        openAction = QAction(QIcon('open.png'), 'Open movie', self)
        openAction.setShortcut('Ctrl+O')
        openAction.setStatusTip('Open movie')
        openAction.triggered.connect(self.openFile)

        menuBar = self.menuBar()
        fileMenu = menuBar.addMenu('File')
        fileMenu.addAction(openAction)

        controlLayout = QHBoxLayout()
        controlLayout.setContentsMargins(0, 0, 0, 0)
        controlLayout.addWidget(self.start_button)
        controlLayout.addWidget(self.pause_button)
        controlLayout.addWidget(cancel_button)
        controlLayout.addStretch()

        wid = QWidget(self)
        self.setCentralWidget(wid)

        layout = QVBoxLayout()
        layout.addLayout(Logos_Layout)
        layout.addLayout(NewboardLayout)
        layout.addLayout(controlLayout)
        layout.addWidget(time_slider)

        wid.setLayout(layout)

    def openFile(self):
        global filename
        filename, _ = QFileDialog.getOpenFileName(self, "Open Movie",
                QDir.currentPath(), "MP4 files (*.mp4)")
        if(filename != ''):
            self.capture.getVideo()

    def openModel(self):
        global modelName, model_list, importFlag
        modelName, _ = QFileDialog.getOpenFileName(self, "Open Model",
                QDir.currentPath(), "H5 files (*.h5)")
        if (modelName != ''):
            model_list[self.comboBox.count()] = modelName
            self.model_label.setText(self.getFileName(modelName))
            temp = cv2.imread(modelName + ".png", 1)
            temp = cv2.cvtColor(temp, cv2.COLOR_BGR2RGB)
            temp = cv2.resize(temp, (281,101))
            height, width, channel = temp.shape
            bytesPerLine = 3 * width
            timg = QtGui.QImage(temp.data, width, height, bytesPerLine, QImage.Format_RGB888)
            pix = QPixmap.fromImage(timg)
            self.model_picture_label.setPixmap(pix)
            if (importFlag == False):
                self.picture_label.clear()
                importFlag = True
            else:
                importFlag = False


    def getFileName(self, path):
        i = len(path) - 1
        while(path[i] != '/'):
            i -= 1
        return '...' + path[i:len(path)]

    def openPicture(self):
        global pictureName, model_list, importFlag
        pictureName, _ = QFileDialog.getOpenFileName(self, "Open Logo",
                QDir.currentPath(), "Images (*.png *.jpg *.jpeg *.bmp)")
        if(pictureName != ''):
            temp = cv2.imread(pictureName, 1)
            temp = cv2.cvtColor(temp, cv2.COLOR_BGR2RGB)
            temp = cv2.resize(temp, (281,101))
            height, width, channel = temp.shape
            bytesPerLine = 3 * width
            timg = QtGui.QImage(temp.data, width, height, bytesPerLine, QImage.Format_RGB888)
            pix = QPixmap.fromImage(timg)
            self.picture_label.setPixmap(pix)
            if (importFlag == False):
                self.model_picture_label.clear()
                importFlag = True
            else:
                importFlag = False

    def logoChanged(self, index):
        global maskMode, logo, model, model_list, model_picture
        maskMode = index
        if(index < len(model_list)):
            temp = logo[index]
            temp = cv2.cvtColor(temp, cv2.COLOR_BGR2RGB)
            temp = cv2.resize(temp, (281,101))
            height, width, channel = temp.shape
            bytesPerLine = 3 * width
            timg = QtGui.QImage(temp.data, width, height, bytesPerLine, QImage.Format_RGB888)
            pix = QPixmap.fromImage(timg)
            self.picture_label.setPixmap(pix)
            temp = model_picture[index]
            temp = cv2.cvtColor(temp, cv2.COLOR_BGR2RGB)
            temp = cv2.resize(temp, (281,101))
            height, width, channel = temp.shape
            bytesPerLine = 3 * width
            timg = QtGui.QImage(temp.data, width, height, bytesPerLine, QImage.Format_RGB888)
            pix = QPixmap.fromImage(timg)
            self.model_picture_label.setPixmap(pix)
        else:
            temp = QPixmap('D:/0330/Preview.png')
            self.picture_label.setPixmap(temp)
            self.model_label.setText("")

    def AddLogo(self):
        global pictureName, modelName, model_picture
        if (self.comboBox.count() < 4):
            check = str(self.Namebox.text())
            if (check and modelName != '' and pictureName != ''):
                temp = cv2.imread(modelName + ".png", 1)
                self.capture.setModel()
                self.capture.setLogo()
                model_picture.append(temp)
                self.comboBox.addItem(self.Namebox.text())
                self.Namebox.setText("")
                self.comboBox.setCurrentIndex(self.comboBox.count() - 1)
                self.model_label.setText("")
            elif (modelName == ''):
                msg = QMessageBox()
                msg.setIcon(QMessageBox.Warning)
                msg.setText("沒有匯入模型")
                msg.setWindowTitle("無法新增")
                msg.setStandardButtons(QMessageBox.Ok)
                msg.exec_()
            elif (pictureName == ''):
                msg = QMessageBox()
                msg.setIcon(QMessageBox.Warning)
                msg.setText("沒有匯入圖片")
                msg.setWindowTitle("無法新增")
                msg.setStandardButtons(QMessageBox.Ok)
                msg.exec_()
            else:
                msg = QMessageBox()
                msg.setIcon(QMessageBox.Warning)
                msg.setText("沒有輸入名稱")
                msg.setWindowTitle("無法新增")
                msg.setStandardButtons(QMessageBox.Ok)
                msg.exec_()
        else:
            msg = QMessageBox()
            msg.setIcon(QMessageBox.Warning)
            msg.setText("最多只能新增4項")
            msg.setWindowTitle("無法新增")
            msg.setStandardButtons(QMessageBox.Ok)
            msg.exec_()

if __name__ == '__main__':
    app = QApplication(sys.argv)
    player = Window()
    player.setFixedSize(900, 290)
    #player.setFixedSize(450,145)
    player.show()
    sys.exit(app.exec_())
