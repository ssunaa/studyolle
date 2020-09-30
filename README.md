# 스프링과 JPA 기반 웹 애플리케이션 프로그래밍 


### TODO LIST



- [ ] TAG 엔티티 Account와 관계 없이 변경하기.(@ManyToMany 지양하는입장에서)
- [ ] h2 콘솔창 접근 안됨.(시큐리티관련) -> 시큐리티쪽 변경했는데 안됨..... 이유찾아볼것.
- [ ] StudyController 스터디 관련 테스트코드 작성 필요.
- [ ] EventController 모임 관련 테스트코드 작성 필요.
- [ ] 도커 설치 필요.
- [ ] 테스트 DB PostgreSQL로 전환.(application-test.yaml )
- [ ] 모임알람 비공개일때는 알람 기능 동작 안되게 수정 필요.
- [X] 모임알람 웹일때 DB 저장로직 필요.
- [X] 모임 비공개 기능 추가하기 -> 공개,비공개,종료 세가지타입으로 설정
- [X] 프로필 이미지 크기 클때 저장안됨.
- [X] 이메일인증시 확인링크에 null 날라오는 것 확인할것 (SendSignUpConfirmEmail메서드 url 이 안날라가는것 같음.) -> application에 host선언안했던 문제
- [X] @PathVariable 바인딩 변경시 오류발생 -> 스프링부트 2.3.1 버전 버그로 인해 발생한 문제로 최신버전으로 수정함.


