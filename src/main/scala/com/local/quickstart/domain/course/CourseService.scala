package com.local.quickstart.domain.course

import cats.Monad
import cats.data.EitherT
import com.local.quickstart.domain.{GenericService, ValidationError}

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 07/04/2018
  */
class CourseService[F[_]](accountRepo: CourseRepositoryAlgebra[F],
                           validation: CourseValidationAlgebra[F])
    extends GenericService[F, Course] {

  override def create(o: Course)(implicit M: Monad[F]): EitherT[F, ValidationError, Course] =
    for {
      _     <- validation.checkModel(o)
      saved <- EitherT.liftF(accountRepo.create(o))
    } yield saved

  override def getAll(implicit M: Monad[F]): EitherT[F, _, List[Course]] =
    EitherT.liftF(accountRepo.getAll)
}


