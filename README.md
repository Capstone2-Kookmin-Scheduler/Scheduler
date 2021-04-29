## 1. 프로젝트 소개


'조금 편리한 스케줄러' 는 그저 일정을 관리하고 알람으로 알려주는 것 뿐만 아니라, 그 일정에 장소를 추가하여 그 장소까지 현재 위치에서부터 대중교통을 탄 시간으로 계산하여 미리 알람을 주는 서비스입니다.

예상시간이 12시이고 강남역으로 가야하는 일정을 추가하였고, 예상되는 출발지로부터 소요시간을 계산합니다. 12시에서부터 예상된 소요시간을 앞당긴 시간에 약 30분가량 더 앞당겨 한번더 현재위치로부터 소요시간을 계산하고 그에 맞게 알람을 띄워줍니다.

알람을 클릭하면 현재 위치에서부터 대중교통의 첫 시작점 (버스정류장, 지하철역)까지 도보거리를 지도상에 띄워줍니다. 

![img1](https://user-images.githubusercontent.com/38371711/116515007-b49cb980-a906-11eb-8ebd-3fb0e1804837.PNG)

또한, 탑승/환승해야할 대중교통 리스트를 보여줍니다.

## 2. 개발환경 

<pre>
Android, JAVA
DB : FirebaseRealtime Database
API : googlePlace, OdySayLab, 공공데이터 포털 API, Mapbox, Google Direction
</pre>
![img3](https://user-images.githubusercontent.com/38371711/116515013-b5355000-a906-11eb-93cb-fc314cc6445b.PNG)

<pre>
DB 구조
</pre>

![img7](https://user-images.githubusercontent.com/38371711/116515021-b8304080-a906-11eb-850f-0457d18d8b8b.PNG)

## 3. 실행 화면 

<pre>
1. 회원가입 및 로그인 화면
</pre>
![img4](https://user-images.githubusercontent.com/38371711/116515015-b5cde680-a906-11eb-9311-c8febccc4c5f.PNG)
<pre>
2. 일정 추가 및 관리 
</pre>
![img8](https://user-images.githubusercontent.com/38371711/116515024-b8c8d700-a906-11eb-9b1e-8495dd83b358.PNG)
![img9](https://user-images.githubusercontent.com/38371711/116515025-b9616d80-a906-11eb-8b35-ee55eb889934.PNG)
