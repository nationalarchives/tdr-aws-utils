package uk.gov.nationalarchives.aws.utils

import java.util.concurrent.CompletableFuture

object TestUtils {
  //Temporary function for running test on Jenkins
  //Jenkins running on Java 8 which does not include the CompletableFuture.failedFuture method
  //See: https://stackoverflow.com/questions/57151079/java8-unittesting-completablefuture-exception
  def failedFuture[T](ex: Throwable): CompletableFuture[T] = {
    // copied from Java 9 https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/util/concurrent/CompletableFuture.html#failedFuture(java.lang.Throwable)
    val f = new CompletableFuture[T]
    f.completeExceptionally(ex)
    f
  }
}
