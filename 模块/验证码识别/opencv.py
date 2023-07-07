import cv2
gaussian_blur_kernel_size=(5,5)
gaussian_blur_sigma_x=0
canny_threshold1=200
canny_threshold2=450
#高斯滤波处理
def get_gaussian_blur_image(image):
    return cv2.GaussianBlur(image,gaussian_blur_kernel_size,gaussian_blur_sigma_x)
#边缘检测处理
def get_canny_image(img):
    return cv2.Canny(img,canny_threshold1,canny_threshold2)
#轮廓提取处理
def get_contours(img):
    contours,_=cv2.findContours(img,cv2.RETR_CCOMP,cv2.CHAIN_APPROX_SIMPLE)
    return contours
#读取图像，函数调用，处理图像
img_raw=cv2.imread('./huadong.png')
img_height,img_width,_=img_raw.shape
img_gaussian_blur=get_gaussian_blur_image(img_raw)
img_canny=get_canny_image(img_gaussian_blur)
contours=get_contours(img_canny)
#设置面积的阈值
def get_contour_area_threshold(img_width,img_height):
    contour_area_min=(img_width*0.15)*(img_height*0.25)*0.8
    contour_area_max=(img_width*0.15)*(img_height*0.25)*1.2
    return contour_area_min,contour_area_max
#设置周长的阈值
def get_arc_length_threshold(img_width,img_height):
    arc_length_min=((img_width*0.15)+(img_height*0.25))*2*0.8
    arc_length_max = ((img_width * 0.15) + (img_height * 0.25)) * 2 * 1.2
    return arc_length_min,arc_length_max
#设置偏移量的阈值
def get_offset_threshold(img_width):
    offset_min=0.2*img_width
    offset_max=0.85*img_width
    return offset_min,offset_max

#函数调用
contour_area_min,contour_area_max=get_contour_area_threshold(img_width=img_width,img_height=img_height)
arc_length_min,arc_length_max=get_arc_length_threshold(img_width,img_height)
offset_min,offset_max=get_offset_threshold(img_width)
offset=None
#for循环contours 进行判断取出符合条件的值
for contour in contours:
    #外接矩形的轮廓数值
    x,y,w,h=cv2.boundingRect(contour)
    if contour_area_min<cv2.contourArea(contour)<contour_area_max and arc_length_min<cv2.arcLength(contour,True)<arc_length_max and offset_min<x<offset_max:
        #对图像进行标注
        cv2.rectangle(img_raw,(x,y),(x+w,y+h),(0,0,255),2)
        offset=x
cv2.imwrite('img_label.png',img_raw)
print(offset)



