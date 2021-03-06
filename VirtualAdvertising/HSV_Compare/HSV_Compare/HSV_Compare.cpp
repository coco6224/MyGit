// HSV_Compare.cpp: 定義主控台應用程式的進入點。
//

#include "stdafx.h"
#include<opencv2\opencv.hpp>
#include<iostream>
using namespace std;
using namespace cv;

void adjustValue(int &h, int&s, int&v) {
	if (h > 180) {
		h = 0;
	}
	else if (h < 0) {
		h = 180;
	}
	if (s > 255) {
		s = 0;
	}
	else if (s < 0) {
		s = 255;
	}
	if (v > 255) {
		v = 0;
	}
	else if (v < 0) {
		v = 255;
	}
}

int main()
{
	int ah = 0, as = 0, av = 0, bh = 0, bs = 0, bv = 0;
	
	Mat a(300, 300, CV_8UC3, Scalar(ah, as, av));
	Mat b(300, 300, CV_8UC3, Scalar(bh, bs, bv));
	
	Mat aShow, bShow;
	cvtColor(a, aShow, CV_HSV2BGR);
	cvtColor(b, bShow, CV_HSV2BGR);

	while (char c = waitKey(1)) {
		if (c == 'a' || c == 's' || c == 'd' || c == 'q' || c == 'w' || c == 'e') {
			switch (c) {
			case 'a':
				ah--;
				break;
			case 's':
				as--;
				break;
			case 'd':
				av--;
				break;
			case 'q':
				ah++;
				break;
			case 'w':
				as++;
				break;
			case 'e':
				av++;
				break;
			}
			adjustValue(ah, as, av);
			a = Mat(300, 300, CV_8UC3, Scalar(ah, as, av));
			cvtColor(a, aShow, CV_HSV2BGR);
			cout << "a: " << ah << " " << as << " " << av << endl;
			cout << "b: " << bh << " " << bs << " " << bv << endl << endl;
		}
		else if (c == 'j' || c == 'k' || c == 'l' || c == 'u' || c == 'i' || c == 'o') {
			switch (c) {
			case 'j':
				bh--;
				break;
			case 'k':
				bs--;
				break;
			case 'l':
				bv--;
				break;
			case 'u':
				bh++;
				break;
			case 'i':
				bs++;
				break;
			case 'o':
				bv++;
				break;
			}

			adjustValue(bh, bs, bv);
			b = Mat(300, 300, CV_8UC3, Scalar(bh, bs, bv));
			cvtColor(b, bShow, CV_HSV2BGR);
			cout << "a: " << ah << " " << as << " " << av << endl;
			cout << "b: " << bh << " " << bs << " " << bv << endl << endl;
		}

		imshow("a", aShow);
		imshow("b", bShow);
	}

    return 0;
}

