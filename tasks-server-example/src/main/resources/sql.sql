CREATE TABLE `jobs_schedule` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `addTime` datetime DEFAULT NULL,
  `deleteStatus` bit(1) DEFAULT b'0',
  `cronExpression` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `distributedRuntime` bit(1) DEFAULT b'0',
  `jobGroup` varchar(255) DEFAULT NULL,
  `jobName` varchar(255) DEFAULT NULL,
  `jobStatus` varchar(255) DEFAULT NULL,
  `lastRunTime` datetime DEFAULT NULL,
  `methodName` varchar(255) DEFAULT NULL,
  `planNextRunTime` datetime DEFAULT NULL,
  `saveLog` bit(1) DEFAULT b'0',
  `sendFailEmail` bit(1) DEFAULT b'0',
  `sendSuccessEmail` bit(1) DEFAULT b'0',
  `springId` varchar(255) DEFAULT NULL,
  `strategy` varchar(255) DEFAULT NULL,
  `updateCronExpressionTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8;

insert into jobs_schedule values(1,now(),false,'0/6 * * * * ?','描述',false,'test','pushjob',
'1',null,'execute',null,false,false,false,'pushjob',null,null);
