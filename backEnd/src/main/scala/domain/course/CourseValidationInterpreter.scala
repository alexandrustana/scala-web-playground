package domain.course

import cats._
import cats.data.EitherT
import cats.implicits._
import domain.{AlreadyExistsError, DoesNotExistError, InvalidModelError}
import util.Check._
import util.Check.CheckOps._

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 07/04/2018
  */
class CourseValidationInterpreter[F[_]: Monad](courseRepo: CourseRepositoryAlgebra[F])
    extends CourseValidationAlgebra[F] {

  override def doesNotExist(course: Course) = EitherT {
    courseRepo.findByName(course.name).map {
      case None    => Right(())
      case Some(_) => Left(AlreadyExistsError(course))
    }
  }

  override def doesExist(course: Course) = EitherT {
    courseRepo.findByName(course.name).map {
      case None    => Left(DoesNotExistError(course))
      case Some(_) => Right(())
    }
  }

  override def checkModel(course: Course) =
    EitherT.fromEither {
      CourseValidationInterpreter.checkModel(course) match {
        case Left(value) => Left(InvalidModelError(value.toList))
        case Right(_)    => Right(())
      }
    }
}

object CourseValidationInterpreter {

  def apply[F[_]: Monad](repo: CourseRepositoryAlgebra[F]): CourseValidationInterpreter[F] =
    new CourseValidationInterpreter[F](repo)

  private val checkName = checkPred(longerThan(3)("Name") and alphanumeric)

  def checkModel(course: Course): Either[Errors, Course] =
    /*_*/
    (Either.right(course.id), checkName(course.name)).mapN(Course)
}
