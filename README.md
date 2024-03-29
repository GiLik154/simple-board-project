# simple-board-project

## 1. 개요

과제로 Post를 만드는과제가 있었다.
파일 업로드를 해야 하고, 이것을 테스트 하는 코드를 작성하였다.

![242609210-b8401a94-32ca-4073-ab6e-e2473993ad6c](https://github.com/GiLik154/simple-board-project/assets/118507239/9b8fb64c-c244-4537-99cb-fd1b58bd7640)


위와 같은 테스트 코드를 작성했다.
`file.createNewFile();` 을 해야 했던 이유는, 파일이 없으면 NPE 가 발생했기 때문이다.
하지만 동생의 코드 리뷰를 받고서는 테스트코드는 어떠한 상황에서도 돌아가야 한다는 말을 들었다.
즉, 위의 테스트 코드는 컴퓨터의 용량이 모두 찼을 경우에는 돌아가지 않는다는 이야기였다.

그렇다면 가상으로 파일을 만들고, 저장해야 하는 코드가 필요했다.
열심히 찾아서 File을 Mocking 하는 방법을 찾아 봤지만, 똑같이 NPE가 발생했다.
그럴 수밖에 없는 게 Mock의 기본은 빈 객체를 반환하는 것이기 때문이다.

동생에게 다시 물어봤고, `@MockBean`을 사용하라는 말을 들었다.

![242609237-1d94d4fe-dd95-47ca-8325-f99f8cb0e12c](https://github.com/GiLik154/simple-board-project/assets/118507239/14f9037c-1945-47a6-9b48-698bf451414b)


(파일이 실제로 생성되기 때문에 `@AfterEach`로 파일을 매번 삭제해줘야 했다.)
(테스트코드도 매우 길어졌다.)

우선 어떻게 사용했는지를 보기 전에 MockBean에 대해서 알아보고 가야 했다.

## 2. MockBean

Mock의 의미는 다들 잘 알듯 ‘흉내내다’라는 뜻을 가지고 있다.
Mock의 기본 객체는 빈 객체로 생성이 되고, 여기에 우리가 해당하는 행동들을 정의해줄 수 있다.
이것을 위해서 `given()` `when()` 이 주로 사용된다.

사실 `given()` `when()` 두 가지 차이를 찾아보려 했지만 정보를 찾는게 쉽지 않았다.

![242609273-abf8b8ae-5054-4be2-953a-8be99e5b87f2](https://github.com/GiLik154/simple-board-project/assets/118507239/2c123cee-f6a3-4979-845e-e6a7a9522e6d)


내가 이해한 건 지 잘 모르겠지만, 처음에는 when()으로만 메소드 호출에 대한 동작을 정의했던 것 같다. 이것은 givne/when/then 의 주석을 사용하는 BDD의 스타일에 어울리지 않기 때문에 given() 메서드를 새로 도입하였다는 이야기인 것 같다.

즉, 기능적으로는 같으나, BDD의 스타일에 더 맞게 적용하기 위해 given()을 도입했다는 이야기 인 것 같다.

(영어를 잘 못해서 더 자세하게 아시는 분은 알려주시면 감사할 것 같습니다.)

## 3. 사용방법

주된 사용방법은
given(메소드(매개변수)).리턴값(); 과 같은 형식으로 이루어진다.

![242609299-4cbde0dc-4a94-4ebd-8c4a-e11a173af441](https://github.com/GiLik154/simple-board-project/assets/118507239/a9ec510c-f347-446a-8760-318ad3bfcaa7)


나의  FileUploader는 String으로 회사명과 File을 지급받는다.
그리고 Return으로는 저장된 파일의 Path가 반환된다.

![242609328-12e3d701-ce91-4abd-9cbd-5ee61d5b8ca2](https://github.com/GiLik154/simple-board-project/assets/118507239/b6372ca3-71b8-47a4-8e52-04af3a469803)

![242609366-abc59f32-d311-4efe-930e-95f4c91f1f63](https://github.com/GiLik154/simple-board-project/assets/118507239/bd27027c-2305-4aec-8b94-79318ba9af1a)

이걸 Mocking 하면 위와 같이 작성할 수 있다.
FileUploader.upload에 어떤 스트링이나 어떤 파일이 들어오면, fileSaved를 반환한다.

![242609387-4e5d95aa-7858-44d4-8b7b-bdb800b1b08d](https://github.com/GiLik154/simple-board-project/assets/118507239/c22350b0-f15c-45c3-b0d3-9b5b7c686bd4)

전체 코드는 아래와 같고

![242609415-dfdb2241-7e26-43a4-80ba-d68366b5a87a](https://github.com/GiLik154/simple-board-project/assets/118507239/fdb43d09-feb0-4b3e-89d6-e74d2d94332a)

테스트 통과도 아주 잘 된다.
위에서 작성했던 파일을 삭제하는 로직과 파일이 몇 개 저장되었는지 확인하던 `assert`는 삭제되었다.

## 4. 주저리 주저리

테스트 코드의 가장 중요한 것은 언제든지 실행이 가능해야 한다는 것이다.
그것을 위해서 많은 것을 적용을 했음에도, 파일 저장에 대해서는 해결하지 못하고 있었는데, 이번에 그 일을 해결할 수 있게 되었다.
Mock이라는게 참 추상적이고, 어려웠다. 정보를 찾아보면 다 각자 다른 이야기를 하고 있어서, 그러한 부분에서 공부하는게 쉽지 않았다.
누군가 알려주지 않는다면, 쉽게 접근할 수 있는 영역처럼 느껴졌다.
그 이유가 너무 광범위하고, 다양한 방법으로 접근할 수 있기 때문이라고 생각했다.

나의 이 글이 초보 개발자에게 도움이 되었으면 좋겠다.

또한 내가 잘못 알고 사용한 부분도 있을 수 있습니다. 이러한 부분에서 고수 개발자분들께서는 많은 조언 부탁드립니다.
