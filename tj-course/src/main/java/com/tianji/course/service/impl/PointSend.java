// public class PointsAndStatusService {

//     private RedissonClient redissonClient;
//     private PointsRepository pointsRepository;
//     private BadgeRepository badgeRepository;
//     private CertificateRepository certificateRepository;

//     public PointsAndStatusService(RedissonClient redissonClient, PointsRepository pointsRepository,
//                                    BadgeRepository badgeRepository, CertificateRepository certificateRepository) {
//         this.redissonClient = redissonClient;
//         this.pointsRepository = pointsRepository;
//         this.badgeRepository = badgeRepository;
//         this.certificateRepository = certificateRepository;
//     }

//     public void handlePointsMessage(Long userId, String actionType, String actionId, int pointsChange, String messageId) {
//         // 锁的粒度基于userId和actionId
//         String lockKey = "points:lock:" + userId + ":" + actionId;
//         RLock lock = redissonClient.getLock(lockKey);

//         try {
//             if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
//                 // 检查是否已经处理过该消息
//                 if (!pointsRepository.isMessageProcessed(messageId)) {
//                     // 根据actionType执行不同的业务逻辑
//                     switch (actionType) {
//                         case "courseCompletion":
//                             handleCourseCompletion(userId, pointsChange);
//                             break;
//                         case "examPassed":
//                             handleExamPassed(userId, actionId, pointsChange);
//                             break;
//                         default:
//                             System.out.println("Unknown action type: " + actionType);
//                     }

//                     // 标记消息为已处理
//                     pointsRepository.markMessageAsProcessed(messageId);
//                 }
//             }
//         } catch (InterruptedException e) {
//             Thread.currentThread().interrupt();
//         } finally {
//             if (lock.isHeldByCurrentThread()) {
//                 lock.unlock();
//             }
//         }
//     }

//     private void handleCourseCompletion(Long userId, int pointsChange) {
//         // 更新积分
//         boolean updateSuccess = pointsRepository.atomicUpdatePoints(userId, pointsChange);
//         if (updateSuccess) {
//             // 检查并更新勋章状态
//             checkAndUpdateBadges(userId);
//         }
//     }

//     private void handleExamPassed(Long userId, String examId, int pointsChange) {
//         // 更新积分
//         boolean updateSuccess = pointsRepository.atomicUpdatePoints(userId, pointsChange);
//         if (updateSuccess) {
//             // 颁发考试证书
//             grantCertificate(userId, examId);
//         }
//     }

//     private void checkAndUpdateBadges(Long userId) {
//         // 查询用户已完成的课程数
//         int completedCourses = pointsRepository.getCompletedCoursesCount(userId);

//         // 根据课程数判断是否授予新的勋章
//         if (completedCourses >= 5 && !badgeRepository.hasBadge(userId, "BEGINNER_BADGE")) {
//             badgeRepository.grantBadge(userId, "BEGINNER_BADGE");
//         }
//         if (completedCourses >= 10 && !badgeRepository.hasBadge(userId, "INTERMEDIATE_BADGE")) {
//             badgeRepository.grantBadge(userId, "INTERMEDIATE_BADGE");
//         }
//         if (completedCourses >= 20 && !badgeRepository.hasBadge(userId, "ADVANCED_BADGE")) {
//             badgeRepository.grantBadge(userId, "ADVANCED_BADGE");
//         }
//     }

//     private void grantCertificate(Long userId, String examId) {
//         // 颁发考试证书
//         certificateRepository.grantCertificate(userId, examId);
//     }
// }

// public void handlePointsMessage(Long userId, String actionType, String actionId, int pointsChange, String messageId) {
//     // 锁的粒度基于userId和actionId
//     String lockKey = "points:lock:" + userId + ":" + actionId;
//     RLock lock = redissonClient.getLock(lockKey);

//     try {
//         if (lock.tryLock(5, 30, TimeUnit.SECONDS)) {
//             // 检查是否已经处理过该消息
//             if (!pointsRepository.isMessageProcessed(messageId)) {
//                 // 根据actionType执行不同的业务逻辑
//                 switch (actionType) {
//                     case "courseCompletion":
//                         handleCourseCompletion(userId, pointsChange);
//                         break;
//                     case "examPassed":
//                         handleExamPassed(userId, actionId, pointsChange);
//                         break;
//                     default:
//                         System.out.println("Unknown action type: " + actionType);
//                 }

//                 // 标记消息为已处理
//                 pointsRepository.markMessageAsProcessed(messageId);
//             }
//         }
//     } catch (InterruptedException e) {
//         Thread.currentThread().interrupt();
//     } finally {
//         if (lock.isHeldByCurrentThread()) {
//             lock.unlock();
//         }
//     }
// }



// private void handleExamPassed(Long userId, String examId, int pointsChange) {
//     // 更新积分
//     boolean updateSuccess = pointsRepository.atomicUpdatePoints(userId, pointsChange);
//     if (updateSuccess) {
//         // 颁发考试证书
//         certificateRepository.grantCertificate(userId, examId);

//         // 发送通知
//         notifyUser(userId, "You have passed the exam and received a certificate!");
//     }
// }