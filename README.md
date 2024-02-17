# RabbitMQ

## erlang cookie

- Cluster간 인증하기 위한 쿠키

## Ram모드

- 큐에 대한 메타정보를 메모리에 저장
- Disk모드에 비해 속도는 빠르나 해당 노드에 문제 발생시 유실 가능

## Exchange 종류

**Fanout**

- exchange와 binding된 모든 큐에 publish

**Direct**

- 디폴트 exchange
- 생성되는 queue가 자동으로 binding되고, 각 queue의 이름이 routing key로 지정

**Topic**

- direct는 routing key가 일치해야 가능하지만 Topic은 routing key 패턴이 일치하는 queue에 publish

**Header**

- key-value로 정의된 헤더에 의해 메세지를 queue에 전달
- 메세지를 전달하는 producer쪽에서 정의하는 header의 key-value와 메세지를 받는 consumer쪽에서 정의된 argument의 key-value가 일치하면 binding

-------

# RabbitMQ 성능 테스트
publish, consumer 모두 spring framework를 사용


## 1. Clustring
Master Node는 Disk모드이고, Slave Node는 Ram모드로 진행

### 1-1. 노드 2개
![2_clustering_detail](https://github.com/siawase7179/RabbitMQ/assets/152139618/82468875-1d03-4b89-ba53-c5974c4134cd)

![2_clustering_performance](https://github.com/siawase7179/RabbitMQ/assets/152139618/e732f048-27b6-4c95-87ee-b614e2220002)

> [!note]
> pulbish와 consumer 모두 8000/s 속도
>
> 원하는 수치가 나오지 않아 혹시나 하는 마음에 **ha mode를 exactly로 바꾸고 ha-params 를 1**로 세팅하였다.
>
> **사실상 mirror를 하지 않겠다는 내용**

![2_not_mirror_detail](https://github.com/siawase7179/RabbitMQ/assets/152139618/140907d6-4d88-4530-807c-c829fe4ffed6)

detail을 보면 mirrors가 없다

![2_not_mirror_performance](https://github.com/siawase7179/RabbitMQ/assets/152139618/0c8291e1-8262-4894-9e34-b44b2abe26b9)

> [!note]
> **역시 미러링을 제거하니 2배가량 성능이 향상되었다.**
>
> 하지만 미러링을 안하면 노드 다운시 문제가 된다.
> 
> 의문점: 클러스터링 노드가 많아지면 어떤 현상이 나타날까??

-------------

### 1-2 노드 3개

역시 Master Node는 Disk모드 Slave Node는 모두 Ram 모드 이다.

![3_clustering_detail](https://github.com/siawase7179/RabbitMQ/assets/152139618/b8f72bf8-2889-4785-b324-00d44505d357)

그림과 같이 Mirros는 두 Slave Node가 보인다.

![3_clustering_performance](https://github.com/siawase7179/RabbitMQ/assets/152139618/a52b8c58-1602-4437-9397-055fb1317bd1)

> [!note]
> **노드 2개에서 클러스터를 엮었을 때보다 성능이 떨어졌다.**
> 
> 떨어진 성능 수치를 보니 미러링을 두군데에 하다보니 그만큼 성능이 감소한 것 같다.
> 
> 역시나 ha모드를 바꿔가며 테스트를 진행했다.

![3_clustering_ha_node_detail](https://github.com/siawase7179/RabbitMQ/assets/152139618/cddd4981-3ddb-4611-af86-bb8a2fe1fd09)

3개의 클러스터링으로 되어있지만 **하나의 노드에만 mirror**를 하겠다는 내용이다.

![3_clustering_ha_node_performance](https://github.com/siawase7179/RabbitMQ/assets/152139618/8a6f20e4-799a-4c28-b38c-57ed2ff32618)

역시 2개의 클러스터링으로 엮은 것과 동일한 성능이 나왔다.



> [!note]
> mirroring의 갯수에 따라 성능이 크게 달라진다.

-------------

## 2. Sharding

구글에서 어느 개발자가 sharding을 이용해 30개 노드로 100만 TPS를 냈다는 블로그를 본적이 있어 sharding을 사용했습니다.

![sharding_detail](https://github.com/siawase7179/RabbitMQ/assets/152139618/d567baee-bb14-450c-b9d0-e33764f828ce)

sharding 설정 참고 : https://github.com/rabbitmq/rabbitmq-sharding(https://github.com/rabbitmq/rabbitmq-sharding)

> 단일 노드
![sharding_1_node_performance](https://github.com/siawase7179/RabbitMQ/assets/152139618/f8a42166-8603-4cbf-b33d-7af180923524)

> mirror 노드 추가
![sharding_2_node_performance](https://github.com/siawase7179/RabbitMQ/assets/152139618/b1b67717-c876-4328-93ef-6a1840bbff2d)

sharding을 하였으나, mirror node를 두지 않았을 경우 높은 성능을 보였다.


> [!note]
> Sharding 처리를 하게 되면 각각 노드별로 분산 처리 되기때문에 성능을 각각 노드에서 낼 수 있지만,
>
> **HA mirroring node를 늘릴수록 성능은 떨어진다.**
> 
> RabbitMQ 특성상 mirror 노드의 영향이 성능에 큰 영향을 미치는 것 같다.

