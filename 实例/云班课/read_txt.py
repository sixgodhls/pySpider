import re
class read_fp:
    # def read_txt1(self,txt_path):
    #     with open(txt_path,'r',encoding='utf-8') as fp:
    #         content=fp.read().replace('\n','')
    #     pattern=re.compile('(简单|一般|困难)(?P<timu>.*?)正确答案(?P<daan>.*?)你的')
    #     res_ques=re.findall(pattern=pattern,string=content)
    #     iter_re_ques=re.finditer(pattern,content)
    #     dict_all={}
    #     dict_true={}
    #     for i in iter_re_ques:
    #         timu=i.group('timu')
    #         daan=re.search(pattern='(\w+)',string=i.group('daan')).group()
    #         if daan=='正确' or daan=='错误':
    #             dict_true[timu]=daan
    #         elif daan!='正确' or daan!='错误':
    #             timu_true = re.search(pattern='(?P<timu>.*?)A', string=timu).group('timu')
    #             pattern=re.compile('A. (?P<A_ans>.*?)B. (?P<B_ans>.*?)C. (?P<C_ans>.*?)D. (?P<D_ans>.*?)$')
    #             item_A=re.search(pattern,timu).group('A_ans')
    #             item_B=re.search(pattern,timu).group('B_ans')
    #             item_C=re.search(pattern,timu).group('C_ans')
    #             item_D=re.search(pattern,timu).group('D_ans')
    #             item_E=re.search(pattern='^(.*?)(E|)(?P<E_ans>.*?)$',string=item_D).group('E_ans').replace('E','')
    #             item_A=re.search(pattern='(\w+)',string=item_A).group()
    #             item_B=re.search(pattern='(\w+)',string=item_B).group()
    #             item_C=re.search(pattern='(\w+)',string=item_C).group()
    #             item_D=re.search(pattern='(\w+)',string=item_D).group().replace('E','')
    #             item_E=re.search(pattern='(\w+)',string=item_E).group()
    #             dict_all[timu_true]={'A':item_A,'B':item_B,'C':item_C,'D':item_D,'E':item_E}
    #             dict_true[timu_true]={}
    #             for i in daan:
    #                 dict_true[timu_true][i] = dict_all[timu_true][i]
    #     return dict_true

    def read_txt(self,txt_path):
        with open(txt_path,'r',encoding='utf-8') as fp:
            content=fp.read().replace('\n','')
        pattern=re.compile('(简单|一般|困难)(?P<timu>.*?)正确答案(?P<daan>.*?)你的')
        iter_re_ques=re.finditer(pattern,content)
        dict_all={}
        dict_true={}
        for i in iter_re_ques:
            timu=i.group('timu')
            daan=re.search(pattern='(\w+)',string=i.group('daan')).group()
            if daan=='正确' or daan=='错误':
                dict_true[timu]=daan
            elif daan!='正确' or daan!='错误':
                pattern = re.compile('[A-Z]. ')
                chance_all=re.findall(pattern, timu)
                pattern=''
                pattern_format='{}'
                a='. '
                for i in chance_all:
                    i=i[0]
                    pattern+=pattern_format.format(f'{i+a}(?P<{i}_ans>.*?)')
                pattern+='$'
                timu_true = re.search(pattern='(?P<timu>.*?)A', string=timu).group('timu')

                dict_all[timu_true] = {}
                for i in daan:

                    dict_all[timu_true][i] = re.search(pattern,timu).group(f'{i}_ans').strip()
                dict_true[timu_true] = {}
                for i in daan:
                    dict_true[timu_true][i] = dict_all[timu_true][i]

        return dict_true



