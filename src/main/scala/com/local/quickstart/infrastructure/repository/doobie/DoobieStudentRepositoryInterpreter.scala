package com.local.quickstart.infrastructure.repository.doobie

import cats._
import cats.implicits._
import com.local.quickstart.domain.student.{Student, StudentRepositoryAlgebra}
import doobie._
import doobie.implicits._

import scala.language.higherKinds

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 07/04/2018
  */
private object StudentSQL {
  def insert(account: Student): Update0 = ???

  def selectAll: Query0[Student] = ???
}

class DoobieStudentRepositoryInterpreter[F[_]: Monad](val xa: Transactor[F])
    extends StudentRepositoryAlgebra[F] {
  import StudentSQL._

  override def create(o: Student): F[Student] =
    insert(o)
      .withUniqueGeneratedKeys[Long]("ID")
      .map(id => o.copy(id = id.some))
      .transact(xa)

  override def getAll: F[List[Student]] = ???
}

object DoobieStudentRepositoryInterpreter {
  def apply[F[_]: Monad](xa: Transactor[F]): DoobieStudentRepositoryInterpreter[F] =
    new DoobieStudentRepositoryInterpreter(xa)
}
