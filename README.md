#使用前请先更改storage.properties中的路径，和PropertyReader中的路径，并根据需要更改其它数据

#StorageService.createFile:传入文件大小、文件种类，即可计算出最佳文件夹，并创建空文件，返回该文件路径，为后续写入做准备
#Storage.getInstance:获取Storage对象，初始化时会根据配置文件建好文件夹，并记录其空间、文件数等指标
#Storage.getWinner:基于“空间权重*空间参数+文件权重*文件参数”对每个文件夹进行打分，将文件写入获胜的文件夹中

#注意：createFile方法只负责创建相应的文件夹及文件，并返回路径，并不负责具体的写入和读出

#具体使用方法参见Main.java