package com.local.quickstart.domain.account

import cats.Monad
import cats.data.EitherT
import com.local.quickstart.domain.{AccountAlreadyExistsError, GenericService}

/**
  * @author Alexandru Stana, alexandru.stana@busymachines.com
  * @since 07/04/2018
  */
class AccountService[F[_]](accountRepo: AccountRepositoryAlgebra[F],
                           validation: AccountValidationAlgebra[F])
    extends GenericService[F, Account] {

  override def create(o: Account)(implicit M: Monad[F]): EitherT[F, AccountAlreadyExistsError, Account] =
    for {
      _     <- validation.doesNotExist(o)
      saved <- EitherT.liftF(accountRepo.create(o))
    } yield saved

  override def getAll(implicit M: Monad[F]): EitherT[F, _, List[Account]] = EitherT.liftF(accountRepo.getAll)
}

object AccountService {
  def apply[F[_]](repositoryAlgebra: AccountRepositoryAlgebra[F],
                  validationAlgebra: AccountValidationAlgebra[F]): AccountService[F] =
    new AccountService[F](repositoryAlgebra, validationAlgebra)
}
