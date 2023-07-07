from urllib.parse import urlencode
import requests
import time
'''
Request URL: https://www.mosoteach.cn/web/index.php?c=interaction_quiz&m=person_result 个人解析
Request URL: https://www.mosoteach.cn/web/index.php?c=interaction_quiz&m=start_quiz 开始答题
Request URL: https://www.mosoteach.cn/web/index.php?c=interaction_quiz&m=save_answer 交卷

id: 1DD56574-0DED-C483-5E11-A666AF5A9940 ==act_id
user_id: 9DDBF645-CF18-4B73-B42D-A225ACCD765C user_id

act_id: 1DD56574-0DED-C483-5E11-A666AF5A9940 题目集id

id: "2210057F-7170-4CBA-A0CD-BE367EE1E891"
clazz_course_id: "2C80F667-CCF0-45A9-9F52-F186C1DF4ED0"

act_id: 2210057F-7170-4CBA-A0CD-BE367EE1E891

Cookie: 
_uab_collina=164518061714283536855822; 
acw_tc=781bad2816661502821696301e0b54e4fb02f6d1bad2cd50c1b9be2348fed1; 
teachweb=f1818ebc8f99f12502382f97e69497df1864a6a7; 
SERVERID=f83e20313967653971d0618a2ae74747|1666151324|1666151221

X-token: 78322d66544cfdfd3ba2b31f0df3a8a5

Cookie: _uab_collina=164518061714283536855822; 
acw_tc=781bad2816661502821696301e0b54e4fb02f6d1bad2cd50c1b9be2348fed1; 
teachweb=f1818ebc8f99f12502382f97e69497df1864a6a7; 
SERVERID=f83e20313967653971d0618a2ae74747|1666151422|1666151221

Cookie: _uab_collina=164518061714283536855822; 
acw_tc=781bad2816661502821696301e0b54e4fb02f6d1bad2cd50c1b9be2348fed1; 
teachweb=f1818ebc8f99f12502382f97e69497df1864a6a7; 
SERVERID=f83e20313967653971d0618a2ae74747|1666151479|1666151221



'''


url='https://www.mosoteach.cn/web/index.php?c=interaction_quiz&m=person_result'
ts=int(time.time())
headers={
    'user-agent': 'Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.131 Safari/537.36 SLBrowser/8.0.0.3161 SLBChan/21',
    # 'Cookie':f'_uab_collina=164518061714283536855822; acw_tc=781bad2816661502821696301e0b54e4fb02f6d1bad2cd50c1b9be2348fed1; teachweb=f1818ebc8f99f12502382f97e69497df1864a6a7; SERVERID=f83e20313967653971d0618a2ae74747|{ts}|1666151221',
    'Cookie': f'_uab_collina=164518061714283536855822; acw_tc=76b20f4316661632264323128e28be45ffe1f4fde097438d7a6c60152b477d; teachweb=300792c5c082815e883a80fa5ee04a4fb9ad8c5c; SERVERID=75616bcd1ea8ec157e112381bf1eec35|{ts}|1666163442',
    'Referer': 'https://www.mosoteach.cn/web/index.php?c=interaction_quiz&m=person_quiz_result&clazz_course_id=B84EE7C3-8177-41A1-B333-0DC96870BE94&id=1DD56574-0DED-C483-5E11-A666AF5A9940&order_item=group',
    # 'X-token': '78322d66544cfdfd3ba2b31f0df3a8a5',
    'Origin': 'https://www.mosoteach.cn',
    'Host': 'www.mosoteach.cn',
    'Content-Type': 'application/x-www-form-urlencoded',
'X-token': '942ffb973bdd1a110b1396c3ff737050'

}

data_dict={
    'id': '1DD56574-0DED-C483-5E11-A666AF5A9940',
    'user_id': '9DDBF645-CF18-4B73-B42D-A225ACCD765C',
}

data=urlencode(data_dict)
response=requests.post(url=url,data=data,headers=headers)
print(response.text)
