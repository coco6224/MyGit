// HEVideo.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<iostream>
#include<opencv2\opencv.hpp>
using namespace std;
using namespace cv;

Mat ori, effect, mask, masked, maskedwhite, white;

void currentFrame(const Mat origin, Mat& effected) {
	
	Mat hsv;
	cvtColor(origin, hsv, CV_BGR2HSV);

	vector<Mat> before, after;

	split(hsv, before);

	after.push_back(before[0]);

	//after.push_back(Mat());
	//equalizeHist(before[0], after[0]);

	after.push_back(Mat());
	equalizeHist(before[1], after[1]);

	after.push_back(Mat());
	equalizeHist(before[2], after[2]);

	merge(after, effected);
	//cvtColor(ori, effect, CV_BGR2HSV);
}

Mat masking(int hup, int hlb, int sup, int slb, int vup, int vlb, Mat temp) {
	Mat mask;
	if (hup > 180) {
		Mat mask1, mask2;
		inRange(temp, Scalar(hlb, slb, vlb), Scalar(181, sup+1, vup+1), mask1);
		inRange(temp, Scalar(0, slb, vlb), Scalar(hup-179, sup+1, vup+1), mask2);
		mask = mask1 + mask2;
	}
	else if (hlb < 0) {
		Mat mask1, mask2;
		inRange(temp, Scalar(181+hlb, slb, vlb), Scalar(181, sup + 1, vup + 1), mask1);
		inRange(temp, Scalar(0, slb, vlb), Scalar(hup+1, sup + 1, vup + 1), mask2);
		mask = mask1 + mask2;
	}
	else {
		inRange(temp, Scalar(hlb, slb, vlb), Scalar(hup + 1, sup + 1, vup + 1), mask);
	}
	return mask;
}

void OnMouse(int event, int x, int y, int flag, void* param) {
	if (event == CV_EVENT_LBUTTONDOWN) {
		currentFrame(ori, effect);
		Vec3b *hsv = effect.ptr<Vec3b>(y, x);
		int h = hsv->val[0];
		int s = hsv->val[1];
		int v = hsv->val[2];

		cout << "H: " << h << endl;
		cout << "S: " << s << endl;
		cout << "V: " << v << endl<< endl;

		int hRange = 5;
		int sRange = 90;
		int vRange = 70;

		if (s <= 30) {//30, 35
			int sup = max(s + 25, 35);
			mask = masking(255, 0, sup, 0, v + vRange, v - vRange, effect);
			cout << "(s<=30)" << endl << endl;
			cout << 255 << "~" << 0 << endl;
			cout << sup << "~" << 0 << endl;
			cout << v + vRange << "~" << v - vRange << endl;
			cout << endl;
		}
		else if (v <= 10) {//40, 45
			mask = masking(h + 20, h - 20, 255, 0, 10, 0, effect);
			cout << "(v<=10)" << endl << endl;
			cout << h + 20 << "~" << h - 20 << endl;
			cout << 255 << "~" << 0 << endl;
			cout << 10 << "~" << 0 << endl;
			cout << endl;
		}
		//add
		else if (s <= 100) {
			mask = masking(h+30, h-30, s + sRange, s-sRange, v + vRange, v - vRange, effect);
			cout << "(s<=100)" << endl;
			cout<<h+30<<"~"<<h-30<< endl;
			cout << s + sRange << "~" << s - sRange << endl;
			cout << v + vRange << "~" << v - vRange << endl;
			cout << endl;
		}
		//end add
		else {
			mask = masking(h + hRange, h - hRange, s + sRange, s - sRange, v + vRange, v - vRange, effect);
			cout << "(else)" << endl << endl;
			cout << h + hRange << "~" << h - hRange << endl;
			cout << s + sRange << "~" << s - sRange << endl;
			cout << v + vRange << "~" << v - vRange << endl;
			cout << endl;
		}

		/* equalization

		int hRange = 15;
		int sRange = 75;
		int vRange = 80;

		if (v <= 40) {
			int upper = max(40, v + 5);
			inRange(effect, Scalar(0, 0, 0), Scalar(255, 255, upper), mask);
		}
		else if (s <= 40){
			/*
			if (h + hRange > 180) {
				Mat mask1, mask2;
				inRange(effect, Scalar(h - hRange, 0, v - vRange), Scalar(181, 50 + 1, 256), mask1);
				inRange(effect, Scalar(0, 0, v - vRange), Scalar(h + hRange - 181, 50 + 1, 256), mask2);
				mask = mask1 + mask2;
			}
			else if (h - hRange < 0) {
				Mat mask1, mask2;
				inRange(effect, Scalar(180 + h - hRange, 0, v - vRange), Scalar(181, 50 + 1, 256), mask1);
				inRange(effect, Scalar(0, 0, v - vRange), Scalar(h + hRange, 50 + 1, 256), mask2);
				mask = mask1 + mask2;
			}
			else {
				inRange(effect, Scalar(h - hRange, 0, v - vRange), Scalar(h + hRange + 1, 50 + 1, 256), mask);
			}
			inRange(effect, Scalar(0, 0, v-vRange), Scalar(255, 51, v+vRange), mask);
		}
		else {
			if (h + hRange > 180) {
				Mat mask1, mask2;
				inRange(effect, Scalar(h - hRange, s - sRange, v - vRange), Scalar(181, s + sRange + 1, v + vRange + 1), mask1);
				inRange(effect, Scalar(0, s - sRange, v - vRange), Scalar(h+hRange-181, s + sRange + 1, v + vRange + 1), mask2);
				mask = mask1 + mask2;
			}
			else if (h - hRange < 0) {
				Mat mask1, mask2;
				inRange(effect, Scalar(180+h - hRange, s - sRange, v - vRange), Scalar(181, s + sRange + 1, v + vRange + 1), mask1);
				inRange(effect, Scalar(0, s - sRange, v - vRange), Scalar(h + hRange, s + sRange + 1, v + vRange + 1), mask2);
				mask = mask1 + mask2;
			}
			else {
				inRange(effect, Scalar(h - hRange, s - sRange, v - vRange), Scalar(h + hRange + 1, s + sRange + 1, v + vRange + 1), mask);
			}
		}*/
		ori.copyTo(masked, mask);
		white.copyTo(maskedwhite, mask);

		imshow("mask", mask);
		imshow("masked", masked);
		imshow("maskedWhite", maskedwhite);

		setMouseCallback("mask", OnMouse);
		setMouseCallback("masked", OnMouse);
		setMouseCallback("maskedWhite", OnMouse);
	}
}

int main()
{
	VideoCapture vc;
	vc.open("test.mp4");
	vc >> ori;
	white = Mat(ori.rows/2, ori.cols/2, CV_8U, Scalar(255));
	while (!ori.empty()) {
		char c = waitKey(1);
		resize(ori, ori, Size(ori.cols/2, ori.rows/2));
		imshow("ori", ori);
		setMouseCallback("ori", OnMouse);
		if (c == 'a') {
			mask = Mat();
			masked = Mat();
			maskedwhite = Mat();
			waitKey();
		}
		vc >> ori;
	}
    return 0;
}

