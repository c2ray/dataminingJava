# 关联规则算法及其测评

## 算法0: [Apriori4j](https://github.com/seratch/apriori4j)

github上的Apriori算法



## 算法1: Apriori






## 算法2: 遗传算法
1. 将每一个item用数字编号, 比如: `{动作=0,休闲=1,大型多人在线=2}`

2. 将每一个项集用步骤一的编号进行排序: `{[0,1], [0,2], [1,2]}`

3. 用 `0,1` 把每一条编号后的数据编码成二进制形式, 比如: 
  `{[0,1] => 110, [0,2] => 101}`

4. 随机生成**n**条**3**位二进制编码 (n为种群数, 3为item的类别数)

5. 评估每一条二进制编码的适应度 (把编码的支持度作为适应度, 最大为1)

6. 种群交叉

7. 种群变异

8. 一直重复5, 6, 7直到达到终止条件 (迭代次数为终止条件)

   

## 使用数据集对算法效率进行测评
