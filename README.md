# mentoree-msa
## 프로젝트 소개  
![프로젝트_구조_및_배포_흐름](https://user-images.githubusercontent.com/65437310/191651170-70a999bb-4924-4716-b877-521054f72f2a.png)  

이전 프로젝트(멘토링 프로그램을 주제로한 게시판 형태의 웹서비스)를 마이크로 서비스 구조로 변경해본 프로젝트입니다.

이번 프로젝트에서는 AWS를 이용한 배포까지 시도해보았습니다.  
  
사이트: [Mentoree](https://mentoree.tk)  
  
해당 프로젝트에 대한 부가적인 설명은 블로그에 글을 작성했습니다.  
  
블로그: [프로젝트 변경점](https://devcabinet.tistory.com/58)

## 프로젝트 목표  
많은 기업들이 변화의 흐름에 대응하고, 서비스의 확장을 유연하게 하기 위해 마이크로 서비스를 도입하는 추세인 것 같습니다.  
그래서 이번 프로젝트는 기존의 모놀리식 아키텍처에서 마이크로 서비스 아키텍처로 변경함으로써, 마이크로 서비스의 기본적인 흐름을 파악하는 것을 중점으로 두었습니다.   
또한 마이크로 서비스가 동작하면서 도와주는 부가적인 인프라에 대해서 사용해보는 것 또한 목표로 설정하였습니다. 
마지막으로 아마존 웹서비스(AWS)를 이용해서 배포를 함으로써, 서비스 구축부터 배포까지의 흐름을 경험하는 것이 이번 프로젝트의 목표입니다. 

--- 사용 해 본 기술 ---  
개발환경: Jdk 11, Gradle 7, Spring boot 2.7.0, Spring cloud 3.1.1  
DB관련: JPA, QueryDSL, MariaDB, Redis
Infra 관련: 
- Spring Cloud (Eureka, Config, Gateway)
- Kafka, RabbitMQ
- AWS(EC2, RDS, S3)
- Travis

## 프로젝트 구성

![프로젝트_흐름도](https://user-images.githubusercontent.com/65437310/191651253-827292df-96a9-4a9b-a39e-a57ecf02cbb2.png)    

프로젝트 구성  
  
![배포_흐름도](https://user-images.githubusercontent.com/65437310/191651315-402ca9fe-4835-490d-99f2-7a0c0af9903a.png)  
CI/CD 구성  


## 변경점

이전 프로젝트((깃허브)[https://github.com/JeminLim/mentoree])와 비교한 변경점입니다.  
  
자세한 내용은 [블로그](https://devcabinet.tistory.com/58)에 작성했습니다.  

1. 모놀리식 -> 마이크로 서비스 구조  
![마이크로서비스 프로젝트 구조](https://user-images.githubusercontent.com/65437310/191652061-f77386ff-bb0f-477e-9dab-112ad23a8fed.png)
- 기존의 모놀리식 구조에서 마이크로 서비스 구조로 변경하여, 각 서비스들의 데이터베이스는 논리적으로 독립되어 있습니다.  
그래서 각 서비스들은 내부적으로 Feign Client와 Kafka를 이용해서 통신을 이용한 데이터를 전달하거나 이벤트를 발생시켜 동작을 하는 구조로 구성해보았습니다.  

2. 멀티모듈 구조 도입  
![멀티모듈](https://user-images.githubusercontent.com/65437310/191652728-377fce17-5f36-47ea-8f4c-602dc0302415.png)  
- 여러 서비스로 분할이 되면 서 공통적으로 사용해야 하는 코드들이 생기기 시작했으며, 최대한 중복을 피하고자 멀티모듈로 프로젝트를 구성해보았습니다. 

3. 로그인 포커스 변경  
- 기존 프로젝트에서는 단순 회원가입을 통해 암호화 및 DB에 저장하는 방식 + JWT 토큰을 사용했었습니다. 이번 프로젝트에서는 현재 상용 서비스들이 사용하고 있는
OAuth에 포커스를 하여 OAuth + JWT 토큰 을 통한 로그인 구현에 집중했습니다. 
- 기존 Security의 설정을 통해서 진행했던 OAuth를 사용했지만, 이번에는 OAuth 토큰을 통한 인증을 내부적으로 한번 구현함으로써 어떻게 구현이 되는지 이해해보고자 직접적으로 구현해보았습니다.   
4. 다양한 기술 스택 맛보기
- 깊게 공부했다고는 할 수 없지만, 다양한 기술에 대해서 기본적으로 사용해봄으로써 어떠한 기능을 하는지에 대해서 공부하고자 사용해보았습니다.  
사용해본 기술로는 MSA를 구성하기 위한 Spring cloud, Redis, Kafka가 있으며, 배포를 위한 CI/CD 구축을 위해서는 Travis를 깃허브와 연동, AWS의 S3와 codedeploy를 사용해서 구축해보았습니다.   


## Endpoint 
이전 프로젝트에서는 Swagger를 통해서 API를 문서화 하였지만, 이번에는 Spring rest docs를 사용해보았습니다.   
프로젝트의 구성 도메인 서비스, Member / Mentoring / Reply 3가지로 구분하였으며, 각각의 API에 대한 문서를 다음 주소에서 확인하실 수 있습니다. 
  
- Member-service: https://mentoree.tk/api/documents/member
- Mentoring-service: https://mentoree.tk/api/documents/mentoring
- Reply-service: https://mentoree.tk/api/documents/reply

