;first make sure the number of arguments passed into the scripts is more than 1
If $CmdLine[0]<2 Then Exit EndIf ;if parmas num <2 ,then break
;$CmdLine[0] ;����������
;$CmdLine[1] ;��һ������ (�ű����ƺ���)
;$CmdLine[2] ;�ڶ�������
;���Ǵ�cmd�������
 handleUpload($CmdLine[1],$CmdLine[2])

;�����ϴ���������������������һ������������֣��ڶ��������ļ�·��
 Func handleUpload($browser, $uploadfile)
	 Dim $title                          ;����һ��title����
            ;���ݵ�����title���ж���ʲô�����
            If $browser="ie" Then                  		; ����IE�����
				  $title="ѡ��Ҫ���ص��ļ�"
			ElseIf $browser="chrome" Then               ; ����ȸ������
				 $title="��"
			ElseIf	$browser="firefox" Then 			; �����������
				  $title="�ļ��ϴ�"
            EndIf

            if WinWait($title,"",4) Then ;�ȴ��������֣����ȴ�ʱ����4��
                   WinActivate($title)                  ;�ҵ���������֮�󣬼��ǰ����
                   ControlSetText($title,"","Edit1",$uploadfile)   ;���ļ�·�����������
                   ControlClick($title,"","Button1")                ;���������ߴ򿪻����ϴ���ť
            Else
	        Return False
            EndIf
 EndFunc