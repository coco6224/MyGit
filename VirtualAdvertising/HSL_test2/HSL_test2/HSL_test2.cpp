// HSL_test2.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<opencv2\opencv.hpp>
#include<iostream>
#include<math.h>
using namespace cv;
using namespace std;

int X, Y;
bool flag = false;

Mat inRange(Mat input, int a, int b, int LLB, int LUB) {
	Mat output;
	inRange(input, Scalar(0, LUB, 0), Scalar(180, LUB, a*LUB + b), output);
	for (int i = LLB; i < LUB; i++) {
		Mat temp;
		inRange(input, Scalar(0, i, 0), Scalar(180, i, a*i + b), temp);
		output += temp;
	}
	return output;
}

string IntToString(int num)
{
	stringstream ss;
	ss << num;
	string s = ss.str();
	return s;
}

void OnMouse(int event, int x, int y, int flags, void*param) {
	if (event == CV_EVENT_LBUTTONDOWN) {
		X = x;
		Y = y;
		flag = true;
	}
}

int main()
{
	
	int lh = 0;
	int ls = 0;
	int ll = 255;
	int rh = 0;
	int rs = 0;
	int rl = 255;
	
	Mat left(300, 300, CV_8UC3, Scalar(lh, ll, ls));
	Mat right(300, 300, CV_8UC3, Scalar(rh, rl, rs));
	Mat lShow, rShow;

	cvtColor(left, lShow, CV_HLS2BGR);
	cvtColor(right, rShow, CV_HLS2BGR);

	while (char c = waitKey(1)) {
		if (c == 'a'||c=='s'||c=='d' || c == 'q' || c == 'w' || c == 'e') {
			if (c == 'a') {
				lh--;
			}
			else if (c == 's') {
				ll--;
			}
			else if (c == 'd') {
				ls--;
			}
			else if (c == 'q') {
				lh++;
			}
			else if (c == 'w') {
				ll++;
			}
			else if (c == 'e') {
				ls++;
			}

			if (lh > 180) {
				lh = 0;
			}
			else if (lh <0) {
				lh = 180;
			}
			if (ll> 255) {
				ll = 0;
			}
			else if (ll <0) {
				ll = 255;
			}
			if (ls > 255) {
				ls = 0;
			}
			else if (ls <0) {
				ls = 255;
			}
			left=(300, 300, CV_8UC3, Scalar(lh, ll, ls));
			cvtColor(left, lShow, CV_HLS2BGR);
			cout << "left: " << lh << " " << ll << " " << ls << endl;
			cout << "right: " << rh << " " << rl << " " << rs << endl << endl;
		}
		else if (c == 'j' || c == 'k' || c == 'l'|| c == 'u' || c == 'i' || c == 'o') {
			if (c == 'j') {
				rh--;
			}
			else if (c == 'k') {
				rl--;
			}
			else if (c == 'l') {
				rs--;
			}
			else if (c == 'u') {
				rh++;
			}
			else if (c == 'i') {
				rl++;
			}
			else if (c == 'o') {
				rs++;
			}
			if (rh > 180) {
				rh = 0;
			}
			else if (rh <0) {
				rh = 180;
			}
			if (rl > 255) {
				rl = 0;
			}
			else if (rl <0) {
				rl = 255;
			}
			if (rs > 255) {
				rs = 0;
			}
			else if (rs <0) {
				rs = 255;
			}
			right = (300, 300, CV_8UC3, Scalar(rh, rl, rs));
			cvtColor(right, rShow, CV_HLS2BGR);
			cout << "left: " << lh << " " << ll << " " << ls << endl;
			cout << "right: " << rh << " " << rl << " " << rs << endl << endl;
		}
		imshow("left", lShow);
		imshow("right", rShow);
	}
	/*
	Mat ori;
	//ori = imread("part.png");
	//if(ori.cols>500)resize(ori, ori, Size(0.5*ori.cols, 0.5*ori.rows));
	//ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (2).png");
	Mat temp;
	//cout << ori.cols << " " << ori.rows << endl;
	Mat show;//(ori.rows, ori.cols, CV_8U, Scalar(0));
	Mat show2;//(ori.cols, ori.rows, CV_8U, Scalar(0));
	int index = 1;
	cout << IntToString(index);
	ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + IntToString(index) + ").png");
	cvtColor(ori, temp, CV_BGR2HLS);

	while (char c = waitKey(1)) {
		imshow("choose color", ori);
		setMouseCallback("choose color", OnMouse);

		if (c == 'a' || c == 's') {
			destroyAllWindows();
			show2.release();
			Mat show2;
			if (index>0 && c == 'a') {
				index--;
				ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + IntToString(index) + ")" + ".png");
				cvtColor(ori, temp, CV_BGR2HLS);
			}
			else if (c == 's') {
				index++;
				ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (" + IntToString(index) + ")" + ".png");
				cvtColor(ori, temp, CV_BGR2HLS);
			}
		}*/
		/*
		if (c == 'a' || c == 's' | c == 'd'||c=='f'||c=='g'||c=='h'||c=='j') {
		destroyAllWindows();
		show2.release();
		Mat show2;
		}
		switch (c) {
		case 'a':
		ori = imread("15.png");
		//resize(ori, ori, Size(0.5*ori.cols, 0.5*ori.rows));
		cvtColor(ori, temp, CV_BGR2HLS);
		break;
		case 's':
		ori = imread("CNBC.jpg");
		resize(ori, ori, Size(0.35*ori.cols, 0.35*ori.rows));
		cvtColor(ori, temp, CV_BGR2HLS);
		break;
		case 'd':
		ori = imread("aa.jpg");
		cvtColor(ori, temp, CV_BGR2HLS);
		break;
		case 'f':
		ori = imread("goya.png");
		cvtColor(ori, temp, CV_BGR2HLS);
		break;
		case 'g':
		ori = imread("part.png");
		cvtColor(ori, temp, CV_BGR2HLS);
		break;
		case 'h':
		ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (40).png");
		cvtColor(ori, temp, CV_BGR2HLS);
		break;
		case 'j':
		ori = imread("D:\\Git\\VirtualAdvertising\\HSL_test\\HSL_test\\set\\negative (2).png");
		cvtColor(ori, temp, CV_BGR2HLS);
		break;
		}*/
		/*
		if (flag) {
			//imshow("temp", temp);
			show.release();
			Mat show;
			Mat mask;
			//mask.create(Size(temp.cols, temp.rows), CV_8U);
			Vec3b hls_temp = temp.at<Vec3b>(Point(X, Y));
			int Htemp = hls_temp.val[0];
			int Ltemp = hls_temp.val[1];
			int Stemp = hls_temp.val[2];

			int whiteL = 245;
			int blackL = 10;
			int grayS = 10;

			int LRange = 25;
			int SRange = 

			int HLB, HUB, LLB, LUB, SLB, SUB;

			if (Ltemp > whiteL || Ltemp < blackL || Stemp < grayS) {
				HLB = 0;
				HUB = 180;
				mask = inRange(temp, Scalar(HLB))
			}

			cout << Htemp << " " << Ltemp << " " << Stemp << endl;

			ori.copyTo(show, mask);
			ori.copyTo(show2, mask);
			//show2 = show2 + mask;
			imshow("show", show);
			imshow("mask", mask);
			imshow("showAll", show2);
			//waitKey(10);
			//waitKey();
			flag = false;
			setMouseCallback("mask", OnMouse);
			setMouseCallback("showAll", OnMouse);
		}
	}*/
    return 0;
}

